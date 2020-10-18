package com.mall.user.services;

import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.IVerifyService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.UserVerifyRequest;
import com.mall.user.dto.UserVerifyResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class VerifyServiceImpl implements IVerifyService {

    @Autowired
    UserVerifyMapper userVerifyMapper;

    @Autowired
    MemberMapper memberMapper;

    @Override
    public UserVerifyResponse verify(UserVerifyRequest userVerifyRequest) {
        UserVerifyResponse userVerifyResponse = new UserVerifyResponse();
        try {
            userVerifyRequest.requestCheck();
        } catch (ValidateException exception) {
            exception.printStackTrace();
            userVerifyResponse.setCode(exception.getErrorCode());
            userVerifyResponse.setMsg(exception.getMessage());
            return userVerifyResponse;
        }

        //todo transaction

        //更新user_verify表
        Example example = new Example(UserVerify.class);
        example.createCriteria().andEqualTo("username", userVerifyRequest.getUserName())
                                .andEqualTo("uuid", userVerifyRequest.getUuid());
        UserVerify userVerify = new UserVerify();
        userVerify.setIsVerify("Y");
        int effectedrows = userVerifyMapper.updateByExampleSelective(userVerify, example);
        if (effectedrows != 1){
            userVerifyResponse.setCode(SysRetCodeConstants.USERVERIFY_INFOR_INVALID.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USERVERIFY_INFOR_INVALID.getMessage());
            return  userVerifyResponse;
        }

        Example memberExample = new Example(Member.class);
        memberExample.createCriteria().andEqualTo("username", userVerifyRequest.getUserName());
        Member member = new Member();
        member.setIsVerified("Y");
        int rows = memberMapper.updateByExampleSelective(member, memberExample);
        if(rows != 1){
            userVerifyResponse.setCode(SysRetCodeConstants.USERVERIFY_INFOR_INVALID.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USERVERIFY_INFOR_INVALID.getMessage());
            return  userVerifyResponse;
        }

        userVerifyResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        userVerifyResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return userVerifyResponse;
    }
}
