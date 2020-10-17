package com.mall.shopping;

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
}
