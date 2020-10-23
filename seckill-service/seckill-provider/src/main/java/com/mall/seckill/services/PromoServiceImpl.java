package com.mall.seckill.services;

import com.mall.commons.tool.exception.BaseBusinessException;
import com.mall.seckill.PromoService;
import com.mall.seckill.constant.PromoRetCode;
import com.mall.seckill.converter.PromoConverter;
import com.mall.seckill.dal.entitys.PromoItem;
import com.mall.seckill.dal.persistence.PromoMapper;
import com.mall.seckill.dto.*;
import com.mall.seckill.mq.PromoOrderProducer;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailRequest;
import com.mall.shopping.dto.ProductDetailResponse;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    PromoMapper promoMapper;

    @Autowired
    PromoConverter promoConverter;

    @Autowired
    PromoOrderProducer promoOrderProducer;

    @Reference
    IProductService productService;


    @Override
    public SeckillListResponse seckilllist(Integer sessionId) {
        SeckillListResponse seckillListResponse = new SeckillListResponse();
        seckillListResponse.setSessionId(sessionId);
        seckillListResponse.setPsId(sessionId);
        List<SeckillListProductDto> seckillListProductDtoLsit = null;
        try {
            seckillListProductDtoLsit = promoMapper.selectSeckillListProductDto(sessionId);
        } catch (Exception exception) {
            exception.printStackTrace();
            seckillListResponse.setCode(PromoRetCode.DB_EXCEPTION.getCode());
            seckillListResponse.setMsg(PromoRetCode.DB_EXCEPTION.getMessage());
            return seckillListResponse;
        }
        seckillListResponse.setProductList(seckillListProductDtoLsit);
        seckillListResponse.setCode(PromoRetCode.SUCCESS.getCode());
        seckillListResponse.setMsg(PromoRetCode.SUCCESS.getMessage());
        return seckillListResponse;
    }

    @Override
    public PromoProductDetailResponse getPromoProductDetail(PromoProductDetailRequest request) {
        PromoProductDetailResponse response = new PromoProductDetailResponse();

        ProductDetailRequest productDetailRequest = new ProductDetailRequest();
        productDetailRequest.setId(request.getProductId());
        ProductDetailResponse productDetailResponse = productService.getProductDetail(productDetailRequest);
        if(productDetailResponse == null || !productDetailResponse.getCode().equals(PromoRetCode.SUCCESS.getCode())){
            response.setCode(productDetailResponse.getCode());
            response.setMsg(productDetailResponse.getMsg());
            return response;
        }
        Example example = new Example(PromoItem.class);
        example.createCriteria().andEqualTo("psId", request.getPsId())
                                .andEqualTo("itemId", request.getProductId());
        List<PromoItem> promoItemList = promoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(promoItemList)){
            response.setCode(PromoRetCode.SYSTEM_ERROR.getCode());
            response.setMsg(PromoRetCode.SYSTEM_ERROR.getMessage());
            return response;
        }

        PromoProductDetailDTO promoProductDetailDTO = promoConverter.product2PromoDeatilDTO(promoItemList.get(0), productDetailResponse.getProductDetailDto());
        response.setPromoProductDetailDTO(promoProductDetailDTO);
        response.setCode(PromoRetCode.SUCCESS.getCode());
        response.setMsg(PromoRetCode.SUCCESS.getMessage());
        return response;
    }

    @Override
    public CreatePromoOrderResponse createPromoOrder(CreatePromoOrderRequest createPromoOrderRequest) {
        CreatePromoOrderResponse promoOrderResponse = new CreatePromoOrderResponse();
        Boolean result = false;
        try {
            result = promoOrderProducer.sendPromoMessage(createPromoOrderRequest);
        } catch (BaseBusinessException exception) {
            exception.printStackTrace();
            promoOrderResponse.setCode(exception.getErrorCode());
            promoOrderResponse.setMsg(exception.getMessage());
            return promoOrderResponse;
        }
        if(result){
            promoOrderResponse.setCode(PromoRetCode.SUCCESS.getCode());
            promoOrderResponse.setMsg(PromoRetCode.SUCCESS.getMessage());
            return promoOrderResponse;
        }
        promoOrderResponse.setCode(PromoRetCode.SYSTEM_ERROR.getCode());
        promoOrderResponse.setMsg(PromoRetCode.SYSTEM_ERROR.getMessage());
        return promoOrderResponse;
    }

}
