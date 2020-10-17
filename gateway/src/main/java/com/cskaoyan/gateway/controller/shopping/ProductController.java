package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.ProductDetailRequest;
import com.mall.shopping.dto.ProductDetailResponse;
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
}
