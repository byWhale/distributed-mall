package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CartProductDto;
import com.mall.shopping.ICartService;
import com.mall.shopping.dto.ClearCartItemRequest;
import com.mall.shopping.dto.ClearCartItemResponse;
import com.mall.shopping.dto.DeleteCartItemRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/8/1-下午5:05
 * 将购物车中的缓存失效
 */
@Slf4j
@Component
public class ClearCartItemHandler extends AbstractTransHandler {

    @Reference
    ICartService iCartService;

    //是否采用异步方式执行
    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {

        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        DeleteCartItemRequest deleteCartItemRequest = new DeleteCartItemRequest();
        List<CartProductDto> cartProductDtoList = createOrderContext.getCartProductDtoList();

        for (CartProductDto cartProductDto : cartProductDtoList) {
            deleteCartItemRequest.setProductId(cartProductDto.getProductId());
            deleteCartItemRequest.setUserId(createOrderContext.getUserId());
            iCartService.deleteCartItem(deleteCartItemRequest);
        }

        return true;
    }
}
