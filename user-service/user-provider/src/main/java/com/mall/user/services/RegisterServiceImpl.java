package com.mall.user.services;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.RegisterService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    MemberMapper memberMapper;

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

        //todo 发送邮件

        log.info("用户注册成功，注册参数 request:{}", JSON.toJSONString(userRegisterRequest));
        response.setCode(SysRetCodeConstants.SUCCESS.getCode());
        response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return response;
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
