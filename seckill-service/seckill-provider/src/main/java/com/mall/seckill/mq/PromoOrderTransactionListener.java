package com.mall.seckill.mq;

import com.mall.seckill.dal.persistence.PromoMapper;
import com.mall.seckill.dto.CreatePromoOrderRequest;
import com.mall.seckill.services.cache.CacheManager;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PromoOrderTransactionListener implements TransactionListener {

    @Autowired
    PromoMapper promoMapper;

    @Autowired
    CacheManager cacheManager;

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        CreatePromoOrderRequest promoOrderRequest = (CreatePromoOrderRequest) o;

        String key = "promo_order_cache_key_" + message.getTransactionId();

        int effectRows = promoMapper.decreaseStock(promoOrderRequest.getProductId(), promoOrderRequest.getPsId());
        if(effectRows < 1){
            cacheManager.setCache(key, "fail", 3);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt message) {
        String key = "promo_order_cache_key_" + message.getTransactionId();
        String val = cacheManager.checkCache(key);
        if(StringUtils.isEmpty(val)){
            return LocalTransactionState.UNKNOW;
        }
        if(val.equals("fail")){
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        if(val.equals("success")){
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }
}
