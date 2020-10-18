package com.mall.order.dto;/**
 * Created by ciggar on 2019/7/30.
 */

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constant.OrderRetCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午9:49
 */
@Data
public class CreateOrderRequest extends AbstractRequest{

    private Long userId = 62L;

    private Long addressId = 5L;

    private String tel = "18782059038";

    private String userName = "admin";

    private String streetName = "上海青浦区汇联路";

    private BigDecimal orderTotal = new BigDecimal(149);

    List<CartProductDto> cartProductDtoList = new ArrayList<CartProductDto>(){{add(new CartProductDto());}};//购物车中的商品列表

    private String uniqueKey; //业务唯一id


    @Override
    public void requestCheck() {
        if(userId==null||addressId==null||
                StringUtils.isBlank(tel)||StringUtils.isBlank(userName)|| StringUtils.isBlank(streetName)||orderTotal==null){
            throw new ValidateException(OrderRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),OrderRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
