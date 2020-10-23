package com.mall.order;

import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.order.dto.CreateSeckillOrderResponse;

public interface OrderPromoService {
     CreateSeckillOrderResponse createPromoOrder(CreateSeckillOrderRequest seckillOrderRequest);

}
