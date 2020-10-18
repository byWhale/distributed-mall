package com.mall.shopping.services;

import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ItemMapper itemMapper;

    @Override
    public CartListByIdResponse getCartListById(CartListByIdRequest request) {
        CartListByIdResponse response = new CartListByIdResponse();
        Long userId = request.getUserId();
        String userIdRedis = "cart_userId_"+userId;
        RMap<Long,CartProductDto> map = redissonClient.getMap(userIdRedis);
        List<CartProductDto> cartProductDtos = (List)map.readAllValues();
        response.setCartProductDtos(cartProductDtos);
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return response;
    }

    @Override
    public AddCartResponse addToCart(AddCartRequest request) {
        AddCartResponse addCartResponse = new AddCartResponse();
        Long userId = request.getUserId();
        Long productId = request.getProductId();
        Long productNum = request.getProductNum();
        String userIdRedis = "cart_userId_"+userId;
        RMap<Long,CartProductDto> map = redissonClient.getMap(userIdRedis);
        if(map.containsKey(productId)){
            CartProductDto cartProductDto = map.get(productId);
            cartProductDto.setProductNum(cartProductDto.getProductNum()+productNum);
            map.put(productId,cartProductDto);
        }else {
            Item item = itemMapper.selectByPrimaryKey(productId);
            CartProductDto cartProductDto = new CartProductDto();
            cartProductDto.setProductId(item.getId());
            cartProductDto.setSalePrice(item.getPrice());
            cartProductDto.setProductNum(productNum.longValue());
            cartProductDto.setLimitNum(item.getLimitNum().longValue());
            if (item.getStatus()==1){
                cartProductDto.setChecked("true");
            }else {
                cartProductDto.setChecked("false");
            }
            cartProductDto.setProductName(item.getTitle());
            cartProductDto.setProductImg(item.getImage());
            map.put(productId,cartProductDto);
        }
        addCartResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        addCartResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return addCartResponse;
    }

    @Override
    public UpdateCartNumResponse updateCartNum(UpdateCartNumRequest request) {
        UpdateCartNumResponse updateCartNumResponse = new UpdateCartNumResponse();
        Long userId = request.getUserId();
        Long productId = request.getProductId();
        Long productNum = request.getProductNum();
        String userIdRedis = "cart_userId_"+userId;
        RMap<Long,CartProductDto> map = redissonClient.getMap(userIdRedis);
        CartProductDto cartProductDto = map.get(productId);
        cartProductDto.setProductNum(productNum);
        map.put(productId,cartProductDto);
        updateCartNumResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        updateCartNumResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return updateCartNumResponse;
    }

    @Override
    public CheckAllItemResponse checkAllCartItem(CheckAllItemRequest request) {
        return null;
    }

    @Override
    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {
        DeleteCartItemResponse deleteCartItemResponse = new DeleteCartItemResponse();
        Long productId = request.getProductId();
        Long userId = request.getUserId();
        String userIdRedis = "cart_userId_"+userId;
        RMap<Long,CartProductDto> map = redissonClient.getMap(userIdRedis);
        long l = map.fastRemove(productId);
        deleteCartItemResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        deleteCartItemResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return deleteCartItemResponse;
    }

    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        return null;
    }

    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        return null;
    }
}
