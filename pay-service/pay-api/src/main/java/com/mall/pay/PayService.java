package com.mall.pay;

import com.mall.pay.dto.PayRequest;
import com.mall.pay.dto.PayResponse;
import com.mall.pay.dto.QueryStatusResponse;

public interface PayService {
    PayResponse getQRCode(PayRequest payRequest);

    QueryStatusResponse queryStatus(String orderID,PayRequest payRequest,Long userId);
}


