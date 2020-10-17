package com.mall.user.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.constants.SysRetCodeConstants;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

/**
 * create by ciggar on 2020/04/08
 */
@Slf4j
@Builder
public class JwtTokenUtils {
    /**
     * 传输信息，必须是json格式
     */
    private String msg;
    /**
     * 所验证的jwt
     */
    @Setter private String token;

    private final String secret="324iu23094u598ndsofhsiufhaf_+0wq-42q421jiosadiusadiasd";

    public String creatJwtToken () {
        msg = new AESUtil(msg).encrypt();//先对信息进行aes加密(防止被破解） AES 对称加密
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("ciggar").withExpiresAt(DateTime.now().plusDays(1).toDate())
                    .withClaim("user", msg)
                    .sign(Algorithm.HMAC256(secret));
        } catch (Exception e) {
              throw e;
        }
        log.info("加密后：" + token);
        return token;
    }

    public static void main(String[] args) {
//        String token = JwtTokenUtils.builder().msg("cskaoyan").build().creatJwtToken();
//        System.out.println("产生的token:" + token);

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJjaWdnYXIiLCJleHAiOjE2MDI5OTUxNjYsInVzZXIiOiIwQjIzQUZDMkNERUM4QTUzQTVDMjJCNzExMzNCMjg0NjE2NEMzNUI3RjIzRjREQ0MyMkJDQUFGRkNDNjU5MkQxRTI1Qzc0NjM5NTdFRUVFQkZEQzkzNzU3Qzk0QzRFM0IzNTNFMzA5N0RDQzE3NzI2N0U4ODJDRjQyNjhGMEYxQUQwMEY5NzQxNENDNzdBMzgwOEY2MkU3MTY1QjhENDkxNUVEQTI4M0U3NkY2QTRFNUM3MjMwMUNEMEU1MTBGNkJBRURDRkUxNTFDMDM2MUFGNTI2QkRERDY1RDRDMDRGMjM3QzgwQ0QzNTJGMkVEN0ZENkY5QkZBMzQzNTI3QTcyMDZFNUZDNDAyOTFERTQ0RTY0MTNDMjZGMkNENDNDMkI2MTE3NkZDNTI2N0VGRUQ5QkUwMzNGMjFGRDJDQjY2QjcxMjVENEM3RTNDQzcyQUE1NjlDQkZEOUY4QTY3NDU5MkE5NDFGM0Q2NUUwREQ5MzEzQ0VCMkQ5MTE3RDkwOTgzOUY5QjI3QTMxNkFGMTY5NjEyQjYwMTc1MDc0RkE3QjAxM0FDNDlBRjVGQTY4MDAyQzhBRUJEQkFGNUU3Mzg0MERFMjM2QzI1NzQ0MEQxMkJFNDQzNEEyNjc0MDM3M0RFQUQ2OEI0QjZENEI4MjQyRUVGRkRENzhFNENGQzZBNzEzQzI5Njg1QkI5QTJEQzVCMDk5MUQzMzAxOEI0NjRFN0M0MDQwOTgwNjk5NjVBMzQyMzRGOTg1RTZGMjdFMDEifQ.pC3BVaJFHQ1B92STDwx2ihzwKrUK0sW_YiSjubacj10";

        String info = JwtTokenUtils.builder().token(token).build().freeJwt();
        System.out.println(info);

    }


    /**
     * 解密jwt并验证是否正确
     */
    public String freeJwt () {
        DecodedJWT decodedJWT = null;
        try {
            //使用hmac256加密算法
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer("ciggar")
                    .build();
            decodedJWT = verifier.verify(token);
            log.info("签名人：" + decodedJWT.getIssuer() + " 加密方式：" + decodedJWT.getAlgorithm() + " 携带信息：" + decodedJWT.getClaim("user").asString());
        } catch (Exception e) {
            log.info("jwt解密出现错误，jwt或私钥或签证人不正确");
            throw new ValidateException(SysRetCodeConstants.TOKEN_VALID_FAILED.getCode(),SysRetCodeConstants.TOKEN_VALID_FAILED.getMessage());
        }
        //获得token的头部，载荷和签名，只对比头部和载荷
        String [] headPayload = token.split("\\.");
        //获得jwt解密后头部
        String header = decodedJWT.getHeader();
        //获得jwt解密后载荷
        String payload = decodedJWT.getPayload();
        if(!header.equals(headPayload[0]) && !payload.equals(headPayload[1])){
            throw new ValidateException(SysRetCodeConstants.TOKEN_VALID_FAILED.getCode(),SysRetCodeConstants.TOKEN_VALID_FAILED.getMessage());
        }
        return new AESUtil(decodedJWT.getClaim("user").asString()).decrypt();
    }

}
