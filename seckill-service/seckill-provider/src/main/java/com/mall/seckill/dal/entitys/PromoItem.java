package com.mall.seckill.dal.entitys;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "tb_promo_item")
@Data
public class PromoItem {

    @Id
    Integer id;

    @Column(name = "ps_id")
    Integer psId;

    @Column(name = "item_id")
    Integer itemId;

    @Column(name = "seckill_price")
    BigDecimal seckillPrice;

    @Column(name = "item_stock")
    Integer itemStock;
}
