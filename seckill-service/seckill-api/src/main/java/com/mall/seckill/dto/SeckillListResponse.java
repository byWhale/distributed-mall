package com.mall.seckill.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.util.List;

@Data
public class SeckillListResponse extends AbstractResponse {

    private int psId;
    private int sessionId;
    private List<SeckillListProductDto> productList;

}
