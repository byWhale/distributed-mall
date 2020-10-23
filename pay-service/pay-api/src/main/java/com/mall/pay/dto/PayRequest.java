package com.mall.pay.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;

import com.mall.pay.constants.PayRetCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest extends AbstractRequest {

    private  String nickname;
    private BigDecimal money;
    private  String info;
    private  String orderId;
    private  String payType;
    @Override
    public void requestCheck() {
        if (money==null|| StringUtils.isBlank(nickname)||StringUtils.isBlank(info)||StringUtils.isBlank(orderId)
        ||StringUtils.isBlank(payType)){
            throw new ValidateException(PayRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),PayRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
