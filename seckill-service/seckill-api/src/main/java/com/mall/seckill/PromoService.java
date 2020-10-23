package com.mall.seckill;

import com.mall.seckill.dto.*;

public interface PromoService {

    SeckillListResponse seckilllist(Integer sessionId);

    PromoProductDetailResponse getPromoProductDetail(PromoProductDetailRequest request);


    CreatePromoOrderResponse createPromoOrder(CreatePromoOrderRequest createPromoOrderRequest);
}
