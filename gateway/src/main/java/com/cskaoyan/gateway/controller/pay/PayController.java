package com.cskaoyan.gateway.controller.pay;


import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.commons.tool.utils.CookieUtil;
import com.mall.pay.PayService;
import com.mall.pay.constants.PayRetCode;
import com.mall.pay.dto.PayRequest;
import com.mall.pay.dto.PayResponse;
import com.mall.pay.dto.QueryStatusResponse;
import com.mall.user.IVerifyService;
import com.mall.user.LoginService;
import com.mall.user.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/cashier")
public class PayController {
    PayRequest payrequest;
    @Reference(check = false,retries = 0)
    PayService payService;
    @Reference(check = false,retries = 0)
    LoginService loginService;
    @Reference(check = false)
    RegisterService registerService;
    @Reference(check = false)
    IVerifyService iVerifyService;

    @PostMapping("/pay")
    public ResponseData payOrder(@RequestBody PayRequest request){
        PayResponse response = new PayResponse();
        try {
            request.requestCheck();
        }catch (ValidateException e){
            response.setCode(PayRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            response.setMsg(PayRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            log.error("occur Exception:"+e);
        }
        payrequest=request;
        response=payService.getQRCode(request);
        if (!response.getCode().equals(PayRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(PayRetCode.SYSTEM_ERROR.getMessage());
        }
        return  new ResponseUtil<>().setData(response.getQrCode());
    }
    @GetMapping("/queryStatus")
    public ResponseData queryStatus(String orderId, HttpServletRequest request){
        String token = CookieUtil.getCookieValue(request, "access_token");
        Long userID = iVerifyService.getUserIDByToken(token);
        if (StringUtils.isBlank(orderId)){
            return new ResponseUtil<>().setErrorMsg(PayRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
        QueryStatusResponse response = payService.queryStatus(orderId, payrequest, userID);
        if (!response.getCode().equals(PayRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }
        return new ResponseUtil<>().setData(null);
    }
}
