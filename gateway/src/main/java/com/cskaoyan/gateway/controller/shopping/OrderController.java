package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.user.annotation.Anoymous;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.mall.user.intercepter.TokenIntercepter.USER_INFO_KEY;

@Slf4j
@RestController
@RequestMapping("/shopping")
@Api(tags = "OrderController", description = "订单控制层")
@Anoymous
public class OrderController {

    @Reference(timeout = 3000, check = false, retries = 0)
    OrderCoreService orderCoreService;

    @Reference(timeout = 3000, check = false)
    OrderQueryService orderQueryService;

    /**
     * 创建订单
     */
    @PostMapping("order")
    public ResponseData orderCreate(@RequestBody CreateOrderRequest createOrderRequest, HttpServletRequest httpServletRequest) {

        String userInfo = (String) httpServletRequest.getAttribute(USER_INFO_KEY);
        JSONObject userInfoJson = JSON.parseObject(userInfo);
        Long userId = Long.parseLong(userInfoJson.get("id").toString());
        createOrderRequest.setUserId(userId);

        CreateOrderResponse response = orderCoreService.createOrder(createOrderRequest);
        if (!response.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }

        return new ResponseUtil().setData(response.getOrderId());

    }

    /**
     * 获取当前用户的所有订单
     */
    @GetMapping("order")
    public ResponseData queryOrders(Integer page, Integer size, String sort, HttpServletRequest httpServletRequest) {

        String userInfo = (String) httpServletRequest.getAttribute(USER_INFO_KEY);
        JSONObject userInfoJson = JSON.parseObject(userInfo);
        Long userId = Long.parseLong(userInfoJson.get("id").toString());

        OrderListRequest orderListRequest = new OrderListRequest();
        orderListRequest.setPage(page);
        orderListRequest.setSize(size);
        orderListRequest.setSort(sort);
        orderListRequest.setUserId(userId);

        OrderListResponse response = orderQueryService.queryAll(orderListRequest);
        if (!response.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }

        return new ResponseUtil().setData(response);

    }

    /**
     * 查询订单详情
     */
    @GetMapping("/order/{id}")
    public ResponseData queryOrderDetail(@PathVariable("id") String orderId, HttpServletRequest httpServletRequest) {

        String userInfo = (String) httpServletRequest.getAttribute(USER_INFO_KEY);
        JSONObject userInfoJson = JSON.parseObject(userInfo);
        Long userId = Long.parseLong(userInfoJson.get("id").toString());
        String userName = (String) userInfoJson.get("username");

        OrderDetailResultResponse response = orderQueryService.queryOrderDetail(orderId, userName, userId);
        if (!response.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }

        return new ResponseUtil().setData(response);
    }


    /**
     * 删除订单
     *
     */
    @DeleteMapping("/order/{id}")
    public ResponseData deleteOrder(@PathVariable("id") String orderId,HttpServletRequest httpServletRequest) {

        String userInfo = (String) httpServletRequest.getAttribute(USER_INFO_KEY);
        JSONObject userInfoJson = JSON.parseObject(userInfo);
        Long userId = Long.parseLong(userInfoJson.get("id").toString());

        DeleteOrderRequest deleteOrderRequest = new DeleteOrderRequest();
        deleteOrderRequest.setOrderId(orderId);

        DeleteOrderResponse response = orderCoreService.deleteOrder(deleteOrderRequest);
        if (!response.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }

        return new ResponseUtil().setData(response);
    }


    /**
     * 取消订单
     *
     */
    @PutMapping("/cancelOrder")
    public ResponseData cancelOrder(@RequestBody Map<String, String> map, HttpServletRequest httpServletRequest) {
        String userInfo = (String) httpServletRequest.getAttribute(USER_INFO_KEY);
        JSONObject userInfoJson = JSON.parseObject(userInfo);
        Long userId = Long.parseLong(userInfoJson.get("id").toString());

        String orderId = map.get("id");

        CancelOrderRequest cancelOrderRequest = new CancelOrderRequest();
        cancelOrderRequest.setOrderId(orderId);

        CancelOrderResponse response = orderCoreService.cancelOrder(cancelOrderRequest);
        if (!response.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }

        return new ResponseUtil().setData(response);

    }
}
