package com.mall.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.BaseBusinessException;
import com.mall.seckill.constant.PromoRetCode;
import com.mall.seckill.dto.CreatePromoOrderRequest;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.nustaq.kson.JSonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

@Component
public class PromoOrderProducer {

   TransactionMQProducer transactionMQProducer;

   @Autowired
   PromoOrderTransactionListener transactionListener;

   @PostConstruct
   public void init(){
        transactionMQProducer = new TransactionMQProducer("transaction_group");
        transactionMQProducer.setNamesrvAddr("localhost:9876");
        transactionMQProducer.setTransactionListener(transactionListener);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
   }

    public Boolean sendPromoMessage(CreatePromoOrderRequest createPromoOrderRequest) {
        String messageBody = JSON.toJSONString(createPromoOrderRequest);
        Message message = new Message("promo_order", messageBody.getBytes(Charset.forName("utf-8")));
        TransactionSendResult result = null;
        try {
            result = transactionMQProducer.sendMessageInTransaction(message, createPromoOrderRequest);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        if(result == null){
            throw new BaseBusinessException(PromoRetCode.SYSTEM_ERROR.getCode(), PromoRetCode.SYSTEM_ERROR.getMessage());
        }
        LocalTransactionState transactionState = result.getLocalTransactionState();
        if(transactionState.equals(LocalTransactionState.COMMIT_MESSAGE)){
            return true;
        }
        return false;
    }
}
