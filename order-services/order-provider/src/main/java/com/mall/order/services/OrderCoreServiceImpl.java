package com.mall.order.services;

import com.mall.order.OrderCoreService;
import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.factory.OrderProcessPipelineFactory;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:05
 */
@Slf4j
@Component
@Service(cluster = "failfast")
@Transactional
public class OrderCoreServiceImpl implements OrderCoreService {

	@Autowired
	OrderMapper orderMapper;

	@Autowired
	OrderItemMapper orderItemMapper;

	@Autowired
	OrderShippingMapper orderShippingMapper;

	@Autowired
    OrderProcessPipelineFactory orderProcessPipelineFactory;

	@Autowired
	private StockMapper stockMapper;


	/**
	 * 创建订单的处理流程
	 *
	 * @param request
	 * @return
	 */
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest request) {
		CreateOrderResponse response = new CreateOrderResponse();
		try {
			//创建pipeline对象
			TransOutboundInvoker invoker = orderProcessPipelineFactory.build(request);

			//启动pipeline
			invoker.start(); //启动流程（pipeline来处理）

			//获取处理结果
			AbsTransHandlerContext context = invoker.getContext();

			//把处理结果转换为response
			response = (CreateOrderResponse) context.getConvert().convertCtx2Respond(context);
		} catch (Exception e) {
			log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}
		return response;
	}


	@Override
	public DeleteOrderResponse deleteOrder(DeleteOrderRequest deleteOrderRequest) {

		try{
			orderMapper.deleteByPrimaryKey(deleteOrderRequest.getOrderId());

			Example example = new Example(OrderItem.class);
			example.createCriteria().andEqualTo("orderId",deleteOrderRequest.getOrderId());
			orderItemMapper.deleteByExample(example);

			DeleteOrderResponse deleteOrderResponse = new DeleteOrderResponse();
			deleteOrderResponse.setCode(OrderRetCode.SUCCESS.getCode());
			deleteOrderResponse.setMsg(OrderRetCode.SUCCESS.getMessage());
			return deleteOrderResponse;
		}catch (Exception e){
			System.out.println(e.getStackTrace());
		}

		DeleteOrderResponse deleteOrderResponse = new DeleteOrderResponse();
		deleteOrderResponse.setCode(OrderRetCode.PIPELINE_RUN_EXCEPTION.getCode());
		deleteOrderResponse.setMsg(OrderRetCode.PIPELINE_RUN_EXCEPTION.getMessage());
		return deleteOrderResponse;
	}


	@Override
	public CancelOrderResponse cancelOrder(CancelOrderRequest cancelOrderRequest) {

		CancelOrderResponse response = new CancelOrderResponse();
		try {
			//修改库存
			OrderItem orderItem = orderItemMapper.queryByOrderId(cancelOrderRequest.getOrderId()).get(0);
			Long goodsId = orderItem.getItemId();
			Integer num = orderItem.getNum();

			Stock stock = new Stock();
			stock.setItemId(goodsId);
			stock.setLockCount(-num);
			stock.setStockCount(num.longValue());
			stockMapper.updateStock(stock);

			//删除订单
			DeleteOrderRequest deleteOrderRequest = new DeleteOrderRequest();
			deleteOrderRequest.setOrderId(cancelOrderRequest.getOrderId());
			deleteOrder(deleteOrderRequest);

			response.setCode(OrderRetCode.SUCCESS.getCode());
			response.setMsg(OrderRetCode.SUCCESS.getMessage());
			return response;

		} catch (Exception e) {
			log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}

		response.setCode(OrderRetCode.PIPELINE_RUN_EXCEPTION.getCode());
		response.setMsg(OrderRetCode.PIPELINE_RUN_EXCEPTION.getMessage());
		return response;
	}
}
