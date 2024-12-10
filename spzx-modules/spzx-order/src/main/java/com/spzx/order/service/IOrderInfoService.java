package com.spzx.order.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.domain.TradeVo;

public interface IOrderInfoService extends IService<OrderInfo>{
    TradeVo trade();

    Long submitOrder(OrderForm orderForm);

    void cencel(String orderNo);

    OrderInfo getOrderInfo(Long orderId);

    OrderInfo getOrderInfoByOrderNo(String orderNo);

    void updateOrderStaue(String orderNo);
}