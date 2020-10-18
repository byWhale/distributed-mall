package com.mall.order.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetailResultResponse extends AbstractResponse {

    private String userName;

    private BigDecimal orderTotal;

    private Long userId;

    private String tel;

    private String streetName;

    private Integer orderStatus;

    private List<OrderItemDto> goodsList;
}
