package com.mall.pay.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "tb_payment")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    private Integer id;

    private String status;

    @Column(name = "order_id")
    private String OrderId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "pay_no")
    private String payNo;

    @Column(name = "trade_no")
    private String tradeNo;

    @Column(name = "payer_uid")
    private Integer payerUid;

    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "payer_amount")
    private BigDecimal payerAmount;

    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    @Column(name = "pay_way")
    private String payWay;

    @Column(name = "pay_success_time")
    private Date paySuccessTime;

    @Column(name = "complete_time")
    private Date completeTime;

    private String remark;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}
