package com.mall.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.mall.order.OrderPromoService;
import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.order.dto.CreateSeckillOrderResponse;
import com.mall.seckill.PromoService;
import com.mall.seckill.constant.PromoRetCode;
import com.mall.seckill.converter.PromoConverter;
import com.mall.seckill.dal.entitys.PromoItem;
import com.mall.seckill.dal.persistence.PromoMapper;
import com.mall.seckill.dto.CreatePromoOrderRequest;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class PromoOrderConsumer {

    DefaultMQPushConsumer consumer;

    @Autowired
    PromoMapper promoMapper;

    @Autowired
    PromoConverter promoConverter;

    @Reference
    OrderPromoService orderPromoService;

    @PostConstruct
    public void init(){
        consumer = new DefaultMQPushConsumer("promo_consumer");
        consumer.setNamesrvAddr("localhost:9876");

        try {
            consumer.subscribe("promo_order", "*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try {
                    MessageExt messageExt = list.get(0);
                    String bodyStr = new String(messageExt.getBody());

                    CreatePromoOrderRequest promoOrderRequest = JSON.parseObject(bodyStr, CreatePromoOrderRequest.class);

                    //获取商品秒杀价格
                    Example example = new Example(PromoItem.class);
                    example.createCriteria()
                            .andEqualTo("psId", promoOrderRequest.getPsId())
                            .andEqualTo("itemId", promoOrderRequest.getProductId());
                    List<PromoItem> promoItemList = promoMapper.selectByExample(example);
                    if(CollectionUtils.isEmpty(promoItemList)){
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    PromoItem promoItem = promoItemList.get(0);

                    //生成订单
                    CreateSeckillOrderRequest seckillOrderRequest = promoConverter.promo2SeckillRequest(promoOrderRequest, promoItem);
                    CreateSeckillOrderResponse createSeckillOrderResponse = orderPromoService.createPromoOrder(seckillOrderRequest);
                    if(!createSeckillOrderResponse.getCode().equals(PromoRetCode.SUCCESS.getCode())){
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });

        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

}
