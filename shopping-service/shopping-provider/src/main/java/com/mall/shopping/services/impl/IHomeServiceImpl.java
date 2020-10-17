package com.mall.shopping.services.impl;

import com.mall.shopping.IHomeService;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.shopping.dto.PanelContentDto;
import com.mall.shopping.dto.PanelContentItemDto;
import com.mall.shopping.dto.PanelDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
//商品模块接口1和2，主页显示接口
public class IHomeServiceImpl implements IHomeService {
  @Autowired
    PanelMapper panelMapper;
  @Autowired
    PanelContentMapper panelContentMapper;
  @Autowired
    ContentConverter contentConverter;
    @Override
    public HomePageResponse homepage() {
        HomePageResponse homePageResponse = new HomePageResponse();
        HashSet<PanelDto> set = new HashSet<>();
        List<Panel> panels = panelMapper.selectAll();

        for (Panel panel : panels) {
            Integer panelId = panel.getId();
            List<PanelContentItem> panelContentItems = panelContentMapper.selectPanelContentAndProductWithPanelId(panelId);
            panel.setPanelContentItems(panelContentItems);
            PanelDto panelDto = contentConverter.panen2Dto(panel);
            set.add(panelDto);
        }

        homePageResponse.setPanelContentItemDtos(set);
        return homePageResponse;
    }

    @Override
    public List<PanelContentDto> getNavigation() {
        PanelContent panelContent = new PanelContent();
        panelContent.setPanelId(0);
        List<PanelContent> panelContents = panelContentMapper.select(panelContent);
        List<PanelContentDto> panelContentDtos = contentConverter.panelContents2Dto(panelContents);

        return panelContentDtos;
    }
}
