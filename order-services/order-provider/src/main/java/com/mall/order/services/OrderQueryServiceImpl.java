package com.mall.order.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.converter.OrderConverter;
import com.mall.order.dal.entitys.*;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import com.mall.user.constants.SysRetCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ciggar
 * create-date: 2019/7/30-上午10:04
 */
@Slf4j
@Component
@Service
public class OrderQueryServiceImpl implements OrderQueryService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderShippingMapper orderShippingMapper;

    @Autowired
    OrderConverter orderConverter;

    // 查询所有的订单信息
    @Override
    public OrderListVO queryAll(OrderListRequest orderListRequest) {
        List<OrderDetailInfo> orderDetailInfoList = new ArrayList<>();

        Example example = new Example(Order.class);
        //example.setOrderByClause(orderListRequest.getSort());
        example.createCriteria().andEqualTo("userId", orderListRequest.getUserId());

        List<Order> orders = orderMapper.selectByExample(example);
        Long total = ((Integer)orders.size()).longValue();

        PageHelper.startPage(orderListRequest.getPage(), orderListRequest.getSize());
        orders = orderMapper.selectByExample(example);

        for (Order order : orders) {
            //填充到 orderDetailInfo
            OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
            orderDetailInfo.setOrderId(order.getOrderId());
            orderDetailInfo.setPayment(order.getPayment());
            orderDetailInfo.setPostFee(order.getPostFee());
            orderDetailInfo.setStatus(order.getStatus());
            orderDetailInfo.setCreateTime(order.getCreateTime());
            orderDetailInfo.setUpdateTime(order.getUpdateTime());
            orderDetailInfo.setPaymentTime(order.getPaymentTime());
            orderDetailInfo.setConsignTime(order.getConsignTime());
            orderDetailInfo.setEndTime(order.getEndTime());
            orderDetailInfo.setCloseTime(order.getCloseTime());
            orderDetailInfo.setShippingName(order.getShippingName());
            orderDetailInfo.setShippingCode(order.getShippingCode());
            orderDetailInfo.setUserId(order.getUserId());
            orderDetailInfo.setBuyerMessage(order.getBuyerMessage());
            orderDetailInfo.setBuyerNick(order.getBuyerNick());
            orderDetailInfo.setBuyerComment(order.getBuyerComment());


            //查询 orderItem
            List<OrderItem> orderItems = orderItemMapper.queryByOrderId(order.getOrderId());
            List<OrderItemDto> orderItemDtos = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                OrderItemDto item = new OrderItemDto();
                item.setId(orderItem.getId());
                item.setItemId(orderItem.getItemId().toString());
                item.setOrderId(orderItem.getOrderId());
                item.setNum(orderItem.getNum());
                item.setTitle(orderItem.getTitle());
                item.setPrice(new BigDecimal(orderItem.getPrice()));
                item.setTotalFee(new BigDecimal(orderItem.getTotalFee()));
                item.setPicPath(orderItem.getPicPath());
                orderItemDtos.add(item);
            }
            orderDetailInfo.setOrderItemDto(orderItemDtos);

            //查询 order_Shipping
            OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(order.getOrderId());
            OrderShippingDto orderShippingDto = new OrderShippingDto();
            if (orderShipping != null) {
                orderShippingDto.setOrderId(orderShipping.getOrderId());
                orderShippingDto.setReceiverName(orderShipping.getReceiverName());
                orderShippingDto.setReceiverPhone(orderShipping.getReceiverPhone());
                orderShippingDto.setReceiverMobile(orderShipping.getReceiverMobile());
                orderShippingDto.setReceiverState(orderShipping.getReceiverState());
                orderShippingDto.setReceiverCity(orderShipping.getReceiverCity());
                orderShippingDto.setReceiverDistrict(orderShipping.getReceiverDistrict());
                orderShippingDto.setReceiverAddress(orderShipping.getReceiverAddress());
                orderShippingDto.setReceiverZip(orderShipping.getReceiverZip());
                orderDetailInfo.setOrderShippingDto(orderShippingDto);
            }


            orderDetailInfoList.add(orderDetailInfo);
        }



//        PageInfo pageInfo = new PageInfo(orderDetailInfoList);
//        Long total = pageInfo.getTotal();


        OrderListVO orderListResponse = new OrderListVO();

        orderListResponse.setData(orderDetailInfoList);
        orderListResponse.setTotal(total);

        return orderListResponse;


//        PageInfo pageInfo = new PageInfo(orderDetailInfoList);
//        Long total = pageInfo.getTotal();
//
//        OrderListVO orderListResponse = new OrderListVO();
//        orderListResponse.setData(orderDetailInfoList);
//        orderListResponse.setTotal(total);

    }


    //查询订单信息
    @Override
    public OrderDetailResultVO queryOrderDetail(String orderId, String userName, Long userId) {

        OrderDetailResultVO response = new OrderDetailResultVO();
        response.setUserName(userName);
        response.setUserId(userId);

        //查询 order_shipping
        OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(orderId);
        if (orderShipping != null){
            response.setStreetName(orderShipping.getReceiverAddress());
            response.setTel(orderShipping.getReceiverPhone());
        }

        //查询 order_item
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        List<OrderItemDto> orderItemDtos = orderConverter.item2dto(orderItems);
        response.setGoodsList(orderItemDtos);

        //查询 order
        Order order = orderMapper.selectByPrimaryKey(orderId);
        response.setOrderTotal(order.getPayment());
        response.setOrderStatus(order.getStatus());

        return response;
    }
}
