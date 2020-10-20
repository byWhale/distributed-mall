package com.mall.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderListVO implements Serializable {
    private List<OrderDetailInfo> data;

    /**
     * 总记录数
     */
    private Long total;
}
