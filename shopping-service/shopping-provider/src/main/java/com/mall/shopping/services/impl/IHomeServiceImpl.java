package com.mall.shopping.services.impl;

import com.mall.commons.tool.exception.ValidateException;
import com.mall.shopping.IHomeService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
  @Autowired
    RedissonClient redissonClient;

  String useStr="cart_userId_";
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

    //删除购物车选中的商品
    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        DeleteCheckedItemResposne deleteCheckedItemResposne = new DeleteCheckedItemResposne();

        try {
            request.requestCheck();
        }catch (ValidateException e){
            deleteCheckedItemResposne.setCode(e.getErrorCode());
            deleteCheckedItemResposne.setMsg(e.getMessage());
            return deleteCheckedItemResposne;
        }
        RMap<Long, CartProductDto> map = redissonClient.getMap(useStr + request.getUserId().toString());
        Set keys = map.readAllKeySet();
        for (Object key : keys) {

            if (!map.get(key).getChecked().equals("false")){
                map.remove(key);
            }
        }
        deleteCheckedItemResposne.setCode(ShoppingRetCode.SUCCESS.getCode());
        deleteCheckedItemResposne.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return deleteCheckedItemResposne;
    }
}
