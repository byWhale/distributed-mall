package com.mall.user.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.LoginService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.converter.MemberConverter;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;
import com.mall.user.dto.UserLoginRequest;
import com.mall.user.dto.UserLoginResponse;
import com.mall.user.utils.JwtTokenUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    MemberConverter memberConverter;

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest){
        UserLoginResponse loginResponse = new UserLoginResponse();
        userLoginRequest.requestCheck();
        //根据用户名查询用户，如果存在，验证密码是否正确
        String password = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());

        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo("username", userLoginRequest.getUserName())
                                .andEqualTo("password", password);
        List<Member> members = memberMapper.selectByExample(example);

        if(CollectionUtils.isEmpty(members)){
            loginResponse.setCode(SysRetCodeConstants.USERORPASSWORD_ERRROR.getCode());
            loginResponse.setMsg(SysRetCodeConstants.USERORPASSWORD_ERRROR.getMessage());
            return loginResponse;
        }
        if(members.get(0).getIsVerified().equals("N")){
            loginResponse.setCode(SysRetCodeConstants.USER_ISVERFIED_ERROR.getCode());
            loginResponse.setMsg(SysRetCodeConstants.USER_ISVERFIED_ERROR.getMessage());
            return loginResponse;
        }
//        登陆成功，生成token
        loginResponse = memberConverter.member2LoginRes(members.get(0));
        loginResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        loginResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = null;
        try {
            msg = objectMapper.writeValueAsString(members.get(0));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String token = JwtTokenUtils.builder().msg(msg).build().creatJwtToken();
        loginResponse.setToken(token);
        return loginResponse;
    }

    @Override
    public UserLoginResponse login(String userInfo) {

        ObjectMapper objectMapper = new ObjectMapper();
        Member member = null;
        try {
            member = objectMapper.readValue(userInfo, Member.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserLoginResponse response = memberConverter.member2LoginRes(member);
        response.setCode(SysRetCodeConstants.SUCCESS.getCode());
        response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return response;
    }

    @Override
    public CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest) {
        CheckAuthResponse checkAuthResponse = new CheckAuthResponse();
        try {
            checkAuthRequest.requestCheck();
        } catch (ValidateException exception) {
            exception.printStackTrace();
            checkAuthResponse.setCode(exception.getErrorCode());
            checkAuthResponse.setMsg(exception.getMessage());
            return checkAuthResponse;
        }

        String token = checkAuthRequest.getToken();
        String info = JwtTokenUtils.builder().token(token).build().freeJwt();

        checkAuthResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        checkAuthResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        checkAuthResponse.setUserinfo(info);
        return checkAuthResponse;
    }
}
