package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.user.IVerifyService;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.UserVerifyRequest;
import com.mall.user.dto.UserVerifyResponse;
import org.apache.dubbo.config.annotation.Reference;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Anoymous
public class VerifyController {

    @Reference(check = false)
    IVerifyService verifyService;

    @GetMapping("/user/verify")
    public ResponseData verify( Map map){
        String uuid = (String) map.get("uuid");
        String username = (String) map.get("username");
        UserVerifyRequest userVerifyRequest = new UserVerifyRequest();
        userVerifyRequest.setUserName(username);
        userVerifyRequest.setUuid(uuid);


        UserVerifyResponse userVerifyResponse = verifyService.verify(userVerifyRequest);
        if (!userVerifyResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil().setErrorMsg(SysRetCodeConstants.USER_INFOR_INVALID.getMessage());

        }
        return new ResponseUtil().setData(null);
    }
}
