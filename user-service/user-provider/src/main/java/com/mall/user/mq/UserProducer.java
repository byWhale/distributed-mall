package com.mall.user.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

@Component
public class UserProducer {

    private DefaultMQProducer defaultMQProducer;

    // 初始化的方法
    @PostConstruct
    public void init(){
//        System.out.println("xxx");
        defaultMQProducer = new DefaultMQProducer("user_producer");

        defaultMQProducer.setNamesrvAddr("localhost:9876");
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }


    }

    public Boolean sendEamilMessage(String messsage) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        Message message = new Message("email", messsage.getBytes(Charset.forName("utf-8")));
//        message.setDelayTimeLevel();

        defaultMQProducer.send(message);

        return true;
    }

}
