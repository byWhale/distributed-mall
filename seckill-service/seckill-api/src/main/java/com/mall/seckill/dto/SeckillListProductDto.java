package com.mall.seckill.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeckillListProductDto implements Serializable {
    private String picUrl;
    private int price;
    private int seckillPrice;
    private int id;
    private int inventory;
    private String productName;
    private String image;

    public void setImage(String image) {
        this.image = image;
        this.picUrl = getImageBig(image);
    }

    public String getImageBig(String image){
        if (image != null && !"".equals(image)) {
            String[] strings = image.split(",");
            return strings[0];
        }
        return null;
    }
}
