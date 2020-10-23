package com.mall.seckill.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.seckill.dal.entitys.PromoItem;
import com.mall.seckill.dto.SeckillListProductDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PromoMapper extends TkMapper<PromoItem> {
    List<SeckillListProductDto> selectSeckillListProductDto(Integer sessionId);

    int decreaseStock(@Param("productId") Integer productId, @Param("psId") Integer psId);
}
