package com.mall.pay.service;

import com.alipay.demo.trade.Main;
import com.mall.order.OrderQueryService;
import com.mall.pay.PayService;
import com.mall.pay.constants.PayRetCode;
import com.mall.pay.dal.entity.Payment;
import com.mall.pay.dal.persistence.PaymentMapper;
import com.mall.pay.dto.PayRequest;
import com.mall.pay.dto.PayResponse;
import com.mall.pay.dto.QueryStatusResponse;
import com.mall.user.IVerifyService;
import com.mall.user.LoginService;
import com.mall.user.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Service
@Component
@Slf4j
public class PayServiceImpl implements PayService {
    @Autowired
    Main main;
    @Reference(check = false)
    OrderQueryService orderQueryService;
    @Reference(check = false)
    LoginService loginService;
    @Reference(check = false)
    RegisterService registerService;
    @Reference(check = false)
    IVerifyService iVerifyService;
    @Autowired
    PaymentMapper paymentMapper;
    @Override
    public PayResponse getQRCode(PayRequest payRequest) {
        PayResponse response = new PayResponse();
        String result=main.test_trade_precreate(payRequest);
        if (result.contains("qr")){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://localhost:8080/image/").append(result);
            response.setQrCode(stringBuilder.toString());
            response.setCode(PayRetCode.SUCCESS.getCode());
            response.setMsg(PayRetCode.SUCCESS.getMessage());
        }else {
            response.setCode(PayRetCode.PIPELINE_RUN_EXCEPTION.getCode());
            response.setMsg(PayRetCode.PIPELINE_RUN_EXCEPTION.getMessage());
        }
        return response;
    }

    @Override
    public QueryStatusResponse queryStatus(String orderID, PayRequest payRequest, Long userId) {
       Integer status= main.test_trade_query(orderID);
        QueryStatusResponse response = new QueryStatusResponse();
        if (status==1){
            String nickname = payRequest.getNickName();
            String tradeNo = "tradeprecreate" + orderID;
            String remark=null;
            String payWay=null;

            if ("alipay".equals(payRequest.getPayType())){
                payWay="ali_pay";
                remark="支付宝支付";
            }else {
                payWay="wechat_pay";
                remark="微信支付";
            }
            Payment payment = new Payment(null,String.valueOf(1),orderID,payRequest.getInfo(),null,tradeNo,userId.intValue(),nickname,payRequest.getMoney(),payRequest.getMoney(),payWay,new Date(),new Date(),remark,new Date(),new Date());
           //TODO 抓取异常
            paymentMapper.insert(payment);
            //更改order item表
            orderQueryService.updateOrderAfterPay(orderID);
            //更改库存表
            orderQueryService.updateOrderItemAfterPay(orderID);
            response.setCode(PayRetCode.SUCCESS.getCode());
            response.setMsg(PayRetCode.SUCCESS.getMessage());
        }else {
            response.setCode(PayRetCode.SYSTEM_ERROR.getCode());
            response.setMsg(PayRetCode.PIPELINE_RUN_EXCEPTION.getMessage());
        }
        return response;
    }
}
