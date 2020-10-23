package com.mall.seckill.converter;

import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.seckill.dal.entitys.PromoItem;
import com.mall.seckill.dto.CreatePromoOrderRequest;
import com.mall.seckill.dto.PromoProductDetailDTO;
import com.mall.shopping.dto.ProductDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PromoConverter {

    @Mappings({
            @Mapping(source = "promoItem.seckillPrice", target = "promoPrice")
    })
    PromoProductDetailDTO product2PromoDeatilDTO(PromoItem promoItem, ProductDetailDto productDetailDto);

    @Mappings({
            @Mapping(source = "promoItem.seckillPrice", target = "price"),
            @Mapping(source = "promoOrderRequest.userName", target = "username"),
            @Mapping(source = "promoOrderRequest.uid", target = "userId")
    })
    CreateSeckillOrderRequest promo2SeckillRequest(CreatePromoOrderRequest promoOrderRequest, PromoItem promoItem);
}
