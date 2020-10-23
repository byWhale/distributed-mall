package com.mall.user;

import com.mall.user.dto.UserLoginRequest;
import com.mall.user.dto.UserVerifyRequest;
import com.mall.user.dto.UserVerifyResponse;

public interface IVerifyService {
    //用户激活
    UserVerifyResponse verify(UserVerifyRequest userVerifyRequest);

    /**
     * 根据access_token拿到id
     * @param token
     * @return
     */
    Long getUserIDByToken(String token);
}
