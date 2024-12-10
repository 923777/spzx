package com.spzx.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.service.IOrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品规格接口管理")
@RestController

public class OrderInfoController extends BaseController {
    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired

    @GetMapping("/orderInfo/trade")
    @RequiresLogin
    @Operation(summary = "订单结算")
    public AjaxResult trade(){

        return AjaxResult.success( orderInfoService.trade());
    }
    @PostMapping("/orderInfo/submitOrder")
    @RequiresLogin
    @Operation(summary = "提交订单")
    public AjaxResult submitOrder(@RequestBody OrderForm orderForm){

        return AjaxResult.success( orderInfoService.submitOrder(orderForm));
    }
    @GetMapping("/orderInfo/getOrderInfo/{orderId}")
    @RequiresLogin
    @Operation(summary = "获取订单信息")
    public AjaxResult getOrderInfo(@PathVariable("orderId") Long orderId){
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return success( orderInfo);
    }
    @GetMapping("/getOrderInfoByOrderNo/{orderNo}")
    @InnerAuth
    R<OrderInfo> getOrderInfoByOrderNo(@PathVariable("orderNo") String orderNo){
//        OrderInfo orderInfo = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        OrderInfo orderInfo = orderInfoService.getOrderInfoByOrderNo(orderNo);
        return R.ok(orderInfo);

    };
}
