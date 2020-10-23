package com.mall.seckill.converter;

import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.seckill.dal.entitys.PromoItem;
import com.mall.seckill.dto.CreatePromoOrderRequest;
import com.mall.seckill.dto.PromoProductDetailDTO;
import com.mall.shopping.dto.ProductDetailDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-23T20:28:18+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_251 (Oracle Corporation)"
)
@Component
public class PromoConverterImpl implements PromoConverter {

    @Override
    public PromoProductDetailDTO product2PromoDeatilDTO(PromoItem promoItem, ProductDetailDto productDetailDto) {
        if ( promoItem == null && productDetailDto == null ) {
            return null;
        }

        PromoProductDetailDTO promoProductDetailDTO = new PromoProductDetailDTO();

        if ( promoItem != null ) {
            promoProductDetailDTO.setPromoPrice( promoItem.getSeckillPrice() );
        }
        if ( productDetailDto != null ) {
            if ( productDetailDto.getLimitNum() != null ) {
                promoProductDetailDTO.setLimitNum( productDetailDto.getLimitNum().intValue() );
            }
            List<String> list = productDetailDto.getProductImageSmall();
            if ( list != null ) {
                promoProductDetailDTO.setProductImageSmall( new ArrayList<String>( list ) );
            }
            if ( productDetailDto.getProductId() != null ) {
                promoProductDetailDTO.setProductId( productDetailDto.getProductId().intValue() );
            }
            promoProductDetailDTO.setSubTitle( productDetailDto.getSubTitle() );
            promoProductDetailDTO.setSalePrice( productDetailDto.getSalePrice() );
            promoProductDetailDTO.setProductImageBig( productDetailDto.getProductImageBig() );
            promoProductDetailDTO.setDetail( productDetailDto.getDetail() );
            promoProductDetailDTO.setProductName( productDetailDto.getProductName() );
        }

        return promoProductDetailDTO;
    }

    @Override
    public CreateSeckillOrderRequest promo2SeckillRequest(CreatePromoOrderRequest promoOrderRequest, PromoItem promoItem) {
        if ( promoOrderRequest == null && promoItem == null ) {
            return null;
        }

        CreateSeckillOrderRequest createSeckillOrderRequest = new CreateSeckillOrderRequest();

        if ( promoOrderRequest != null ) {
            if ( promoOrderRequest.getUid() != null ) {
                createSeckillOrderRequest.setUserId( promoOrderRequest.getUid().longValue() );
            }
            createSeckillOrderRequest.setUsername( promoOrderRequest.getUserName() );
            if ( promoOrderRequest.getProductId() != null ) {
                createSeckillOrderRequest.setProductId( promoOrderRequest.getProductId().longValue() );
            }
            createSeckillOrderRequest.setStreetName( promoOrderRequest.getStreetName() );
            createSeckillOrderRequest.setAddressId( promoOrderRequest.getAddressId() );
            createSeckillOrderRequest.setTel( promoOrderRequest.getTel() );
        }
        if ( promoItem != null ) {
            createSeckillOrderRequest.setPrice( promoItem.getSeckillPrice() );
        }

        return createSeckillOrderRequest;
    }
}
