package com.mall.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetailResultVO implements Serializable {
    private String userName;

    private BigDecimal orderTotal;

    private Long userId;

    private String tel;

    private Integer orderStatus;

    private String streetName;

    private List<OrderItemDto> goodsList;
}
