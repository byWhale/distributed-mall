package com.mall.order.dto;/**
 * Created by ciggar on 2019/8/1.
 */

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  ciggar
 * create-date: 2019/8/1-下午9:33
 */
@Data
public class CartProductDto implements Serializable{

    private Long productId = 100057401L;

    private BigDecimal salePrice = new BigDecimal(149);

    private Long productNum = 1L;

    private Long limitNum = 100L;

    private String checked = "true";

    private String productName = "Smartisan T恤 迪特拉姆斯";

    private String productImg = "https://resource.smartisan.com/resource/005c65324724692f7c9ba2fc7738db13.png";
}
