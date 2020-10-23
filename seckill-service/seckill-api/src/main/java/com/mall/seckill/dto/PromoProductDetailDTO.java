package com.mall.seckill.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PromoProductDetailDTO implements Serializable {

    private int limitNum;
    private BigDecimal promoPrice;
    private List<String> productImageSmall;
    private int productId;
    private String subTitle;
    private BigDecimal salePrice;
    private String productImageBig;
    private String detail;
    private String productName;

}
