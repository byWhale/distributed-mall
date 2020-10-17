package com.mall.user;

import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;
import com.mall.user.dto.UserLoginRequest;
import com.mall.user.dto.UserLoginResponse;

public interface LoginService {
    UserLoginResponse login(UserLoginRequest userLoginRequest);

    UserLoginResponse login(String userInfo);

    CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest);
}
