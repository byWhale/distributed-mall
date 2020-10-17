package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.CookieUtil;
import com.mall.user.IKaptchaService;
import com.mall.user.LoginService;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.mall.user.intercepter.TokenIntercepter.ACCESS_TOKEN;
import static com.mall.user.intercepter.TokenIntercepter.USER_INFO_KEY;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    IKaptchaService kaptchaService;

    @Reference
    LoginService loginService;

    @Anoymous
    @PostMapping("login")
    public ResponseData login(@RequestBody Map<String, String> map, HttpServletRequest request, HttpServletResponse response){

        String userName = map.get("userName");
        String userPwd = map.get("userPwd");
        String captcha = map.get("captcha");

        //验证码处理
        KaptchaCodeRequest kaptchaCodeRequest = new KaptchaCodeRequest();
        String uuid = CookieUtil.getCookieValue(request, "kaptcha_uuid");
        kaptchaCodeRequest.setUuid(uuid);
        kaptchaCodeRequest.setCode(captcha);
        KaptchaCodeResponse kaptchaCodeResponse = kaptchaService.validateKaptchaCode(kaptchaCodeRequest);
        String code = kaptchaCodeResponse.getCode();
        if(!code.equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(kaptchaCodeResponse.getMsg());
        }

        UserLoginRequest userLoginRequest = new UserLoginRequest(userName, userPwd);
        UserLoginResponse loginResponse = loginService.login(userLoginRequest);
        if(loginResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            Cookie cookie = CookieUtil.genCookieWithDomain(ACCESS_TOKEN, loginResponse.getToken(), 99999, "localhost");
            CookieUtil.setCookie(response, cookie);
            return new ResponseUtil<>().setData(loginResponse);
        }
        return new ResponseUtil<>().setErrorMsg(loginResponse.getMsg());
    }

    @GetMapping("login")
    public ResponseData login(HttpServletRequest request){
        String userInfo = (String) request.getAttribute(USER_INFO_KEY);
        UserLoginResponse response = loginService.login(userInfo);
        if(response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(response);
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

}
