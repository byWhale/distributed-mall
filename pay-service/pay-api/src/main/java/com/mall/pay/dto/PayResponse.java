package com.mall.pay.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

@Data
public class PayResponse extends AbstractResponse {

    String qrCode;
}
