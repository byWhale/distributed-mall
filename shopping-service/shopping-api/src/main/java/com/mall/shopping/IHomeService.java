package com.mall.shopping;

import com.mall.shopping.dto.DeleteCheckedItemRequest;
import com.mall.shopping.dto.DeleteCheckedItemResposne;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.shopping.dto.PanelContentDto;

import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/23-17:16
 */
public interface IHomeService {

    HomePageResponse homepage();


    List<PanelContentDto> getNavigation();

    //删除购物车选中的商品
    DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request);

}
