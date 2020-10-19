package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.annotation.Anoymous;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shopping")
@Anoymous
public class ProductController {

    @Reference
    IProductService productService;

    @GetMapping("product/{id}")
    public ResponseData getProductDetail(@PathVariable("id") Long id){
        ProductDetailRequest request = new ProductDetailRequest();
        request.setId(id);

        ProductDetailResponse response = productService.getProductDetail(request);
        if (!response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }
        return new ResponseUtil().setData(response.getProductDetailDto());
    }

    @GetMapping("goods")
    public ResponseData getAllProduct(Integer page,Integer size,String sort,Integer priceGt,Integer priceLte,Long cid){
        AllProductRequest request = new AllProductRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSort(sort);
        request.setPriceGt(priceGt);
        request.setPriceLte(priceLte);
        request.setCid(cid);
        AllProductResponse response = productService.getAllProduct(request);

        if (!response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }
        AllProductResponseVO allProductResponseVO = new AllProductResponseVO();
        allProductResponseVO.setData(response.getProductDtoList());
        allProductResponseVO.setTotal(response.getTotal());
        return new ResponseUtil().setData(allProductResponseVO);
    }

    @GetMapping("recommend")
    public ResponseData getRecommendGoods(){
        RecommendResponse response = productService.getRecommendGoods();

        if (!response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }

        return new ResponseUtil().setData(response.getPanelContentItemDtos());
    }
}
