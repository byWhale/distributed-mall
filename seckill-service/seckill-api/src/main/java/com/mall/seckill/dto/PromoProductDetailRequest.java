package com.mall.seckill.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromoProductDetailRequest implements Serializable {

    private Long productId;
    private Long psId;

}
