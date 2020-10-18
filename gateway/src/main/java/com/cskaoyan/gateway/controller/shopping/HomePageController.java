package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IContentService;
import com.mall.shopping.IHomeService;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.shopping.dto.PanelContentDto;
import com.mall.user.annotation.Anoymous;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Anoymous
@RestController
@RequestMapping("shopping")
public class HomePageController {

    @Reference(check = false)
    IHomeService iHomeService;
    @Reference(check = false)
    IContentService iContentService;

    //主页显示
    @GetMapping("/homepage")
    public ResponseData homepage(){
        HomePageResponse homepage = iHomeService.homepage();
        return  new ResponseUtil<>().setData(homepage.getPanelContentItemDtos());
    }
    //导航栏显示
   @GetMapping("/navigation")
    public ResponseData navigation(){
       List<PanelContentDto> result = iHomeService.getNavigation();
       ResponseData responseData = new ResponseUtil().setData(result);
       return responseData;
   }
   /*//查看商品详情
    @GetMapping("/product/{id}")
    public ResponseData product(@PathVariable long id){
        return null;
    }*/
}
