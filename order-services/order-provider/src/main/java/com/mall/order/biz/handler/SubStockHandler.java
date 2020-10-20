package com.mall.order.biz.handler;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.CartProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 扣减库存处理器
 * @Author： wz
 * @Date: 2019-09-16 00:03
 **/
@Component
@Slf4j
public class SubStockHandler extends AbstractTransHandler {

    @Autowired
    private StockMapper stockMapper;

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	@Transactional
	public boolean handle(TransHandlerContext context) {

		CreateOrderContext createOrderContext = (CreateOrderContext) context;
		List<CartProductDto> cartProductDtoList = createOrderContext.getCartProductDtoList();

		//检查库存
		List<Long> productIds = new ArrayList<>();
		Long productId = null;
		for (CartProductDto cartProductDto : cartProductDtoList) {
			productId = cartProductDto.getProductId();
			productIds.add(productId);
		}
		List<Stock> stockList = stockMapper.findStocksForUpdate(productIds);
		if (CollectionUtils.isEmpty(stockList)){
			throw new BizException("查询不到所有库存");
		}
		if (stockList.size() != productIds.size()){
			throw new BizException("部分库存查询无果");
		}

		//扣除库存
		for (CartProductDto cartProductDto : cartProductDtoList) {

			Stock stock = new Stock();
			stock.setItemId(cartProductDto.getProductId());
			stock.setLockCount(cartProductDto.getProductNum().intValue());
			stock.setStockCount(-cartProductDto.getProductNum());

			stockMapper.updateStock(stock);
		}
        return true;
	}
}