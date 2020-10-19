package com.mall.shopping.dto;

import lombok.Data;

import java.util.List;

@Data
public class AllProductResponseVO {

    private List<ProductDto> data;

    private Long total;
}
