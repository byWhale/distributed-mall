package com.mall.shopping.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.converter.ProductConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IProductServiceImpl implements IProductService {

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    PanelMapper panelMapper;

    @Autowired
    PanelContentMapper panelContentMapper;

    @Autowired
    ProductConverter productConverter;

    @Autowired
    ContentConverter contentConverter;
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
        if (item == null){
            productDetailResponse.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            productDetailResponse.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            return productDetailResponse;
        }
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
        AllProductResponse allProductResponse = new AllProductResponse();
        //校验
        request.requestCheck();
        //分页
        PageHelper.startPage(request.getPage(),request.getSize());
        //查询
        Example example = new Example(Item.class);
        String prefix = "";

//        if (request.getSort() != null && request.getSort() != ""){
//            prefix = "price";
//        }else {
//            prefix = "created";
//        }

        Integer i = null;
        try {
            i = Integer.valueOf(request.getSort());
            prefix = "price";
        } catch (NumberFormatException e) {
            i = 0;
            prefix = "created";
        }

        String order = i == 1 ? "asc" : "desc";
        Example.Criteria criteria = example.createCriteria();
        if (request.getPriceGt() != null){
            criteria.andGreaterThanOrEqualTo("price",request.getPriceGt());
        }
        if (request.getPriceLte() != null){
            criteria.andLessThanOrEqualTo("price",request.getPriceLte());
        }
        example.setOrderByClause(prefix+" "+order);
        List<Item> items = itemMapper.selectByExample(example);
        List<ProductDto> productDtos = productConverter.items2Dto(items);
        //计算总数
        PageInfo pageInfo = new PageInfo(items);
        long total = pageInfo.getTotal();
        //赋值
        allProductResponse.setProductDtoList(productDtos);
        allProductResponse.setTotal(total);
        allProductResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        allProductResponse.setCode(ShoppingRetCode.SUCCESS.getCode());

        return allProductResponse;
    }

    @Override
    public RecommendResponse getRecommendGoods() {
        RecommendResponse recommendResponse = new RecommendResponse();
        List<Panel> panels = panelMapper.selectPanelContentById(6);
        if (panels == null){
            recommendResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
            recommendResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            return recommendResponse;
        }
        Panel panel = panels.get(0);
        PanelDto panelDto = contentConverter.panen2Dto(panel);
        List<PanelContentItem> panelContentItems = panelContentMapper.selectPanelContentAndProductWithPanelId(6);
        List<PanelContentItemDto> panelContentItemDtos = contentConverter.panelContentItem2Dto(panelContentItems);
        panelDto.setPanelContentItems(panelContentItemDtos);
        Set<PanelDto> panelDtos = new HashSet<PanelDto>();
        panelDtos.add(panelDto);
        recommendResponse.setPanelContentItemDtos(panelDtos);
        recommendResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        recommendResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return recommendResponse;
    }
}
