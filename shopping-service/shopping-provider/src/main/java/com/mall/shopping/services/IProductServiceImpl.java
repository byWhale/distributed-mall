package com.mall.shopping.services;

import com.mall.commons.tool.exception.ValidateException;
import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ProductConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Service
public class IProductServiceImpl implements IProductService {

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ProductConverter productConverter;
    @Override
    public ProductDetailResponse getProductDetail(ProductDetailRequest request) {
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        //校验参数
        try {
            request.requestCheck();
        }catch (ValidateException e){
            e.printStackTrace();
            productDetailResponse.setCode(e.getErrorCode());
            productDetailResponse.setMsg(e.getMessage());
            return productDetailResponse;
        }
        //查询
        Item item = itemMapper.selectByPrimaryKey(request.getId());
        //判空

        //赋值
        ProductDetailDto productDetailDto = new ProductDetailDto();
        productDetailDto.setProductId(item.getId());
        productDetailDto.setSalePrice(item.getPrice());
        productDetailDto.setProductName(item.getTitle());
        productDetailDto.setSubTitle(item.getSellPoint());
        productDetailDto.setProductImageBig(item.getImages()[0]);
        productDetailDto.setDetail("");
        productDetailDto.setProductImageSmall(Arrays.asList(item.getImages()));
        productDetailResponse.setProductDetailDto(productDetailDto);
        productDetailResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        productDetailResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return productDetailResponse;
    }

    @Override
    public AllProductResponse getAllProduct(AllProductRequest request) {
        return null;
    }

    @Override
    public RecommendResponse getRecommendGoods() {
        return null;
    }
}
