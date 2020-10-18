package com.mall.user.services;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.RegisterService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    UserVerifyMapper userVerifyMapper;

    @Autowired
    MailSender mailSender;

    @Override
    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest) {
        UserRegisterResponse response = new UserRegisterResponse();
        userRegisterRequest.requestCheck();
        try {
            volidUserNameRepeat(userRegisterRequest);
        } catch (ValidateException exception) {
            exception.printStackTrace();
            response.setCode(exception.getErrorCode());
            response.setMsg(exception.getMessage());
            return response;
        }

        //向用户表中插入一条记录
        Member member = new Member();
        member.setUsername(userRegisterRequest.getUserName());
        member.setEmail(userRegisterRequest.getEmail());
        member.setPassword(userRegisterRequest.getUserPwd());
        String userPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getUserPwd().getBytes());
        member.setPassword(userPassword);

        member.setCreated(new Date());
        member.setUpdated(new Date());
        member.setIsVerified("N");
        member.setState(1);


        int effectRows = memberMapper.insert(member);
        if(effectRows != 1){
            response.setCode(SysRetCodeConstants.USER_REGISTER_FAILED.getCode());
            response.setMsg(SysRetCodeConstants.USER_REGISTER_FAILED.getMessage());
            return response;
        }

        //向用户验证表插入一条记录
        String key = member.getUsername() + member.getPassword() + UUID.randomUUID().toString();
        String uuid = DigestUtils.md5DigestAsHex(key.getBytes());
        UserVerify userVerify = new UserVerify(null, member.getUsername(), uuid, new Date(), "N", "N");
        int rows = userVerifyMapper.insert(userVerify);
        if(rows != 1){
            response.setCode(SysRetCodeConstants.USER_REGISTER_VERIFY_FAILED.getCode());
            response.setMsg(SysRetCodeConstants.USER_REGISTER_VERIFY_FAILED.getMessage());
            return response;
        }

        //todo 发送邮箱 消息队列
        sendEmail(uuid, userRegisterRequest);

        log.info("用户注册成功，注册参数 request:{}", JSON.toJSONString(userRegisterRequest));
        response.setCode(SysRetCodeConstants.SUCCESS.getCode());
        response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return response;
    }

    private void sendEmail(String uuid, UserRegisterRequest userRegisterRequest) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("买买买用户激活");
        simpleMailMessage.setFrom("weijiangbai@126.com");
        simpleMailMessage.setTo(userRegisterRequest.getEmail());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://localhost:8080/user/verify?uid=").append(uuid)
                .append("&username=").append(userRegisterRequest.getUserName());
        simpleMailMessage.setText(stringBuilder.toString());
        mailSender.send(simpleMailMessage);
    }

    private void volidUserNameRepeat(UserRegisterRequest userRegisterRequest) {
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo("username", userRegisterRequest.getUserName());
        List<Member> members = memberMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(members)){
            throw new ValidateException(SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getCode(), SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }
}
