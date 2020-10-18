package com.mall.order.biz.handler;/**
 * Created by ciggar on 2019/8/1.
 */

import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dal.entitys.OrderShipping;
import com.mall.order.dal.persistence.OrderShippingMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/8/1-下午5:06
 *
 * 处理物流信息（商品寄送的地址）
 */
@Slf4j
@Component
public class LogisticalHandler extends AbstractTransHandler {

    @Resource
    private OrderShippingMapper orderShippingMapper;

    @Override
    public boolean isAsync() {
        return false;
    }

    @SneakyThrows
    @Override
    public boolean handle(TransHandlerContext context) {

        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        List<String> orderIdList = createOrderContext.getOrderIdList();

        for (String orderId : orderIdList) {
            //创建order_shipping记录
            OrderShipping orderShipping = new OrderShipping();
            orderShipping.setOrderId(createOrderContext.getOrderId());
            orderShipping.setReceiverName(createOrderContext.getUserName());
            orderShipping.setReceiverPhone(createOrderContext.getTel());
            orderShipping.setReceiverAddress(createOrderContext.getStreetName());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
            String date = simpleDateFormat.format(new Date());
            Date updateTime = simpleDateFormat.parse(date);

            orderShipping.setCreated(updateTime);
            orderShipping.setUpdated(updateTime);
            int insetStatus = orderShippingMapper.insert(orderShipping);

            if(insetStatus < 1){
                throw  new BizException(OrderRetCode.DB_EXCEPTION.getCode(),OrderRetCode.DB_EXCEPTION.getMessage());
            }
        }

        return true;
    }
}
