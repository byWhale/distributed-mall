package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IProductCateService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.AllProductCateRequest;
import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.user.annotation.Anoymous;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shopping")
@Anoymous
public class ProductCateController {

    @Reference
    IProductCateService iProductCateService;

    @GetMapping("categories")
    public ResponseData getCategories(){
        String sort = "id";
        AllProductCateRequest request = new AllProductCateRequest();
        request.setSort(sort);

        AllProductCateResponse response = iProductCateService.getAllProductCate(request);

        if (!response.getCode() .equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }

        return new ResponseUtil().setData(response.getProductCateDtoList());
    }
}
