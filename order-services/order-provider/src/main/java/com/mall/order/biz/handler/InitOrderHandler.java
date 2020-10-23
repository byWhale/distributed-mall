package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.commons.tool.utils.NumberUtils;
import com.mall.commons.tool.utils.TradeNoUtils;
import com.mall.order.biz.callback.SendEmailCallback;
import com.mall.order.biz.callback.TransCallback;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dto.CartProductDto;
import com.mall.order.utils.GlobalIdGeneratorUtil;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *  ciggar
 * create-date: 2019/8/1-下午5:01
 * 初始化订单 生成订单
 */

@Slf4j
@Component
public class InitOrderHandler extends AbstractTransHandler {

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private OrderMapper orderMapper;

    @Override
    public boolean isAsync() {
        return false;
    }

    @SneakyThrows
    @Override
    public boolean handle(TransHandlerContext context) {

        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        List<CartProductDto> cartProductDtoList = createOrderContext.getCartProductDtoList();

        ArrayList<Long> buyProductIds = new ArrayList<>();
        ArrayList<String> orderIdList = new ArrayList<>();
        //订单id
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyMMddhhmmss");
        String prefix = simpleDate.format(new Date());
        String orderId = prefix + TradeNoUtils.getTwo();
        System.out.println("orderId:  " + orderId);
        orderIdList.add(orderId);
        createOrderContext.setOrderId(orderId);

        for (CartProductDto cartProductDto : cartProductDtoList) {

            //创建order记录
            Order order = new Order();
            order.setOrderId(orderId);
            order.setBuyerNick(createOrderContext.getBuyerNickName());
            order.setPayment(createOrderContext.getOrderTotal());
            order.setStatus(0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
            String date = simpleDateFormat.format(new Date());
            Date updateTime = simpleDateFormat.parse(date);
            order.setCreateTime(updateTime);
            order.setUpdateTime(updateTime);
            order.setUserId(createOrderContext.getUserId());
            orderMapper.insert(order);

            //创建 order_item记录
            SimpleDateFormat simpleDate1 = new SimpleDateFormat("yyMMddhhmmss");
            String prefix1 = simpleDate.format(new Date());
            String orderItemId = prefix + TradeNoUtils.getTwo();
            //String orderItemId = new GlobalIdGeneratorUtil().getMaxSeq();
            System.out.println("orderItemId:  " + orderId);
            OrderItem orderItem = new OrderItem();
            orderItem.setId(orderItemId);
            orderItem.setItemId(cartProductDto.getProductId());
            orderItem.setOrderId(orderId);
            orderItem.setNum(cartProductDto.getProductNum().intValue());
            orderItem.setTitle(cartProductDto.getProductName());
            orderItem.setPrice(cartProductDto.getSalePrice().doubleValue());
            orderItem.setTotalFee(createOrderContext.getOrderTotal().doubleValue());
            orderItem.setPicPath(cartProductDto.getProductImg());
            orderItem.setStatus(1);
            orderItem.setCreateTime(updateTime);
            order.setUpdateTime(updateTime);
            orderItemMapper.insert(orderItem);

            buyProductIds.add(cartProductDto.getProductId());

        }

        createOrderContext.setBuyProductIds(buyProductIds);
        createOrderContext.setOrderIdList(orderIdList);
        return true;
    }
}
