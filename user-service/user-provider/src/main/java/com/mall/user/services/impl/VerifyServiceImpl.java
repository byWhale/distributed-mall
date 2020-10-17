package com.mall.user.services.impl;

import com.mall.user.IVerifyService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.UserVerify;
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
    @Override
    public UserVerifyResponse verify(UserVerifyRequest userVerifyRequest) {
        UserVerifyResponse userVerifyResponse = new UserVerifyResponse();
        userVerifyRequest.requestCheck();
        Example example = new Example(UserVerify.class);
        UserVerify userVerify = new UserVerify();
        userVerify.setIsVerify("Y");
        example.createCriteria().andEqualTo(userVerifyRequest.getUserName()).andEqualTo(userVerifyRequest.getUuid());
        int updateByExample = userVerifyMapper.updateByExample(userVerify, example);
        if (updateByExample!=1){
            userVerifyResponse.setCode(SysRetCodeConstants.USER_INFOR_INVALID.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USER_INFOR_INVALID.getMessage());
        }

        userVerifyResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        userVerifyResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return userVerifyResponse;
    }
}
