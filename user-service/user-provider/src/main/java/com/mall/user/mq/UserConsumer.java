package com.mall.user.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.user.dto.UserRegisterRequest;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class UserConsumer {

    private DefaultMQPushConsumer consumer;

    @Autowired
    MailSender mailSender;

    @PostConstruct
    public void init(){

               // 设置注册中心
        consumer = new DefaultMQPushConsumer("user_consumer");
        consumer.setNamesrvAddr("localhost:9876");

        // 订阅消息
        try {
            consumer.subscribe("email","*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        // 注册消息的监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            // 消费消息
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    MessageExt messageExt = msgs.get(0);

                    byte[] body = messageExt.getBody();

                    String bodyStr = new String(body);

//                    String tags = messageExt.getTags();
//
//                    String topic = messageExt.getTopic();

                    // 执行对应的逻辑
                    sendEmail(bodyStr);
                    System.out.println(bodyStr + "------------"  + System.currentTimeMillis());

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

                }catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });

        // 启动消息的消费者
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }

    private void sendEmail(String msg) {
        //解析消息
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = null;
        try {
            map = objectMapper.readValue(msg, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uuid = (String) map.get("uuid");
        Map request = (Map) map.get("request");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("买买买用户激活");
        simpleMailMessage.setFrom("weijiangbai@126.com");
        simpleMailMessage.setTo((String) request.get("email"));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://localhost:8080/user/verify?uid=").append(uuid)
                .append("&username=").append(request.get("userName"));
        simpleMailMessage.setText(stringBuilder.toString());
        mailSender.send(simpleMailMessage);
    }

}
