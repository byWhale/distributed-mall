package com.mall.seckill.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

@Data
public class CreatePromoOrderResponse extends AbstractResponse {

    private int productId;
    private int inventory;

}
