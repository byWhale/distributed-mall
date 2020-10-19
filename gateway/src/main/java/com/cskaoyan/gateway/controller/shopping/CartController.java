package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.annotation.Anoymous;
import com.mall.user.dto.AddressListRequest;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.mall.user.intercepter.TokenIntercepter.USER_INFO_KEY;

@RestController
@RequestMapping("/shopping")
public class CartController {
    @Reference
    ICartService cartService;

    @PostMapping("/carts")
    public ResponseData addCarts(@RequestBody AddCartRequest request){
        AddCartResponse response = cartService.addToCart(request);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    @GetMapping("/carts")
    public ResponseData cartsList(HttpServletRequest request){
        CartListByIdRequest cartListByIdRequest = new CartListByIdRequest();
        String userInfo = (String) request.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(object.get("id").toString());
        AddressListRequest addressListRequest = new AddressListRequest();
        cartListByIdRequest.setUserId(uid);
        CartListByIdResponse response = cartService.getCartListById(cartListByIdRequest);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getCartProductDtos());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    @PutMapping("/carts")
    public ResponseData updateCart(@RequestBody UpdateCartNumRequest request){
        UpdateCartNumResponse updateCartNumResponse = cartService.updateCartNum(request);
        if (updateCartNumResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(updateCartNumResponse.getMsg());
        }
        return new ResponseUtil().setErrorMsg(updateCartNumResponse.getMsg());
    }

    @DeleteMapping("/carts/{uid}/{pid}")
    public ResponseData deleteCart(@PathVariable("uid")Long uid, @PathVariable("pid")Long pid){
        DeleteCartItemRequest deleteCartItemRequest = new DeleteCartItemRequest();
        deleteCartItemRequest.setUserId(uid);deleteCartItemRequest.setProductId(pid);
        DeleteCartItemResponse deleteCartItemResponse = cartService.deleteCartItem(deleteCartItemRequest);
        if (deleteCartItemResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(deleteCartItemResponse.getMsg());
        }
        return new ResponseUtil().setErrorMsg(deleteCartItemResponse.getMsg());
    }

//    @DeleteMapping("/items/{id}")
    public ResponseData deleteCart(@PathVariable("id")Long id){
        DeleteSelectedCartItemRequest deleteSelectedCartItemRequest = new DeleteSelectedCartItemRequest();
        deleteSelectedCartItemRequest.setId(id);
        DeleteSelectedCartItemResponse deleteSelectedCartItemResponse = cartService.deleteSelectedCartItem(deleteSelectedCartItemRequest);
        if (deleteSelectedCartItemResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(deleteSelectedCartItemResponse.getMsg());
        }
        return new ResponseUtil().setErrorMsg(deleteSelectedCartItemResponse.getMsg());
    }

}
