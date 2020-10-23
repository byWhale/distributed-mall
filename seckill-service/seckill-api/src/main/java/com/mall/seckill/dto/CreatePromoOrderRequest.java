package com.mall.seckill.dto;

import com.mall.commons.result.AbstractRequest;
import lombok.Data;

@Data
public class CreatePromoOrderRequest extends AbstractRequest {

    private String streetName;
    private Integer productId;
    private String tel;
    private Integer psId;
    private String userName;
    private Integer addressId;
    private Integer uid;

    @Override
    public void requestCheck() {

    }

}
