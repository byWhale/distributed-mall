package com.mall.order.services;

import com.mall.order.OrderPromoService;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.handler.InitOrderHandler;
import com.mall.order.biz.handler.LogisticalHandler;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CartProductDto;
import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.order.dto.CreateSeckillOrderResponse;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailDto;
import com.mall.shopping.dto.ProductDetailRequest;
import com.mall.shopping.dto.ProductDetailResponse;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class OrderPromoServiceImpl implements OrderPromoService {

    @Autowired
    InitOrderHandler initOrderHandler;

    @Autowired
    LogisticalHandler logisticalHandler;

    @Reference
    IProductService productService;

    @Override
    public CreateSeckillOrderResponse createPromoOrder(CreateSeckillOrderRequest seckillOrderRequest) {
        CreateSeckillOrderResponse createSeckillOrderResponse = new CreateSeckillOrderResponse();

        ProductDetailRequest productDetailRequest = new ProductDetailRequest();
        productDetailRequest.setId(seckillOrderRequest.getProductId());
        ProductDetailResponse productDetail = productService.getProductDetail(productDetailRequest);
        ProductDetailDto productDetailDto = productDetail.getProductDetailDto();

        CreateOrderContext createOrderContext = new CreateOrderContext();
        List<CartProductDto> cartProductDtoList = new ArrayList();
        CartProductDto cartProductDto = new CartProductDto();

        createOrderContext.setUserId(seckillOrderRequest.getUserId());
        createOrderContext.setBuyerNickName(seckillOrderRequest.getUsername());
        createOrderContext.setOrderTotal(seckillOrderRequest.getPrice());

        cartProductDto.setProductId(seckillOrderRequest.getProductId());
        cartProductDto.setProductNum(1l);
        cartProductDto.setSalePrice(seckillOrderRequest.getPrice());
        cartProductDto.setProductName(productDetailDto.getProductName());
        cartProductDto.setProductImg(productDetailDto.getProductImageBig());

        cartProductDtoList.add(cartProductDto);
        createOrderContext.setCartProductDtoList(cartProductDtoList);

        boolean ret = initOrderHandler.handle(createOrderContext);

        createOrderContext.setStreetName(seckillOrderRequest.getStreetName());
        createOrderContext.setTel(seckillOrderRequest.getTel());

        logisticalHandler.handle(createOrderContext);

        if(ret){
            createSeckillOrderResponse.setCode(OrderRetCode.SUCCESS.getCode());
            createSeckillOrderResponse.setMsg(OrderRetCode.SUCCESS.getMessage());
        }else {
            createSeckillOrderResponse.setCode(OrderRetCode.SYSTEM_ERROR.getCode());
            createSeckillOrderResponse.setMsg(OrderRetCode.SYSTEM_ERROR.getMessage());
        }
        return createSeckillOrderResponse;
    }
}
