package com.mall.shopping.services;

import com.mall.commons.tool.exception.ValidateException;
import com.mall.shopping.IProductCateService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ProductCateConverter;
import com.mall.shopping.dal.entitys.ItemCat;
import com.mall.shopping.dal.persistence.ItemCatMapper;
import com.mall.shopping.dto.AllProductCateRequest;
import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.shopping.dto.ProductCateDto;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class IProductCateServiceImpl implements IProductCateService {

    @Autowired
    ItemCatMapper itemCatMapper;

    @Autowired
    ProductCateConverter productCateConverter;

    @Override
    public AllProductCateResponse getAllProductCate(AllProductCateRequest request) {

        AllProductCateResponse allProductCateResponse = new AllProductCateResponse();
        //校验参数
        try {
            request.requestCheck();
        }catch (ValidateException e){
            e.printStackTrace();
            allProductCateResponse.setCode(e.getErrorCode());
            allProductCateResponse.setMsg(e.getMessage());
            return allProductCateResponse;
        }
        //查询对象
        Example example = new Example(ItemCat.class);
        example.setOrderByClause(request.getSort() + " " + "Asc");
        List<ItemCat> itemCats = itemCatMapper.selectByExample(example);
        //判空
        if (itemCats == null){
            allProductCateResponse.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            allProductCateResponse.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            return allProductCateResponse;
        }
        //赋值
        List<ProductCateDto> productCateDtos = productCateConverter.items2Dto(itemCats);
        allProductCateResponse.setProductCateDtoList(productCateDtos);
        allProductCateResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        allProductCateResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        return allProductCateResponse;
    }
}
