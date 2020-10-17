package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.CookieUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.mall.user.intercepter.TokenIntercepter.ACCESS_TOKEN;

@RestController
@RequestMapping("/user")
public class LoginOutController {

    @RequestMapping("loginOut")
    public ResponseData loginOut(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        HttpSession session = request.getSession();
        //销毁session：
        session.invalidate();
        //清除cookie：
        Cookie cookie = CookieUtil.genCookieWithDomain(ACCESS_TOKEN, null, 0, "localhost");
        response.addCookie(cookie);
        return new ResponseUtil<>().setData(null);
    }
}
