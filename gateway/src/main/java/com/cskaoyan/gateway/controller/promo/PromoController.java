package com.cskaoyan.gateway.controller.promo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.seckill.PromoService;
import com.mall.seckill.dto.*;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/shopping")
public class PromoController {

    @Reference
    PromoService promoService;

    @RequestMapping("seckilllist")
    public ResponseData seckilllist(Integer sessionId){
        SeckillListResponse seckillListResponse = promoService.seckilllist(sessionId);
        if(seckillListResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(seckillListResponse);
        }
        return new ResponseUtil<>().setErrorMsg(seckillListResponse.getMsg());
    }

    @RequestMapping("promoProductDetail")
    public ResponseData promoProductDetail(@RequestBody PromoProductDetailRequest request){
        PromoProductDetailResponse response = promoService.getPromoProductDetail(request);
        if(!response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }
        return new ResponseUtil<>().setData(response);
    }

    @RequestMapping("seckill")
    public ResponseData seckill(@RequestBody CreatePromoOrderRequest createPromoOrderRequest, HttpServletRequest servletRequest){
        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject jsonObject = JSON.parseObject(userInfo);
        createPromoOrderRequest.setUid((Integer) jsonObject.get("id"));

        CreatePromoOrderResponse promoOrderResponse = promoService.createPromoOrder(createPromoOrderRequest);

        if(!promoOrderResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(promoOrderResponse.getMsg());
        }

        return new ResponseUtil<>().setData(promoOrderResponse);
    }
}
