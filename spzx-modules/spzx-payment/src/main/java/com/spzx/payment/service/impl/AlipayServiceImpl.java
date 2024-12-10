package com.spzx.payment.service.impl;

import com.alibaba.fastjson.JSON;


import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.domain.R;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;

import com.spzx.order.api.RemoteOrderService;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.domain.OrderItem;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.mapper.PaymentInfoMapper;
import com.spzx.payment.service.AlipayService;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlipayServiceImpl extends ServiceImpl<PaymentInfoMapper,PaymentInfo> implements AlipayService {

    @Autowired
    RemoteOrderService remoteOrderService;
    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    // 1 阿里网关地址(沙箱)
    public static String serverUrl =  "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 2 合作伙伴id，appId
    public static String appId = "9021000142655903";
    // 3 自己的私钥
    public static String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCL0TR7obUgXM3IEzY9/Q9/jsBIqfR3PSiBW6+4W2DQ8zcqxBpeg/19YreCn1DUXvQ3ucXhf2dxCucWeKXfT0uYorp4XsjyCapN+Kk9Qcv8kMM/yV/NWEG7Raz0F7HMzwpCV8mFDTw61rdgShwaXDG91wDlMTaYNguq63KdJxWnlO+ZocKCs/USFQaCmi1kR3O5NKu6h1+ow709sDUejyC46/14sTEYEipTc+SfG2/6UegVgN1QM65+7aU3lEhMOosu+lsODPHtfzugMLApXfgqhRNIMjz5ei8DoyqG+UkNbpMDp2XueojNC4OKFm62k8giY0OhivC6MS9piRJqZF53AgMBAAECggEATd8eAQGmlLi7cASrQEkymZPTKmVmIAczCSEctGxIdvgPMMV5/kw7IduaY6kB5tZf5K2WNaq03TP6UvytPbu3WfLAMoxwFMYnUTf+YnzD9Q5XgK5SqlbOfVQoKOcNgwmJKDwmo/EkK40tIvcDwfa5SYd3Gy+WvZ3UT8TE2BxNpkCti2jU2IAfPcu6zL6HSNoNowsS5IQEq2s36P1SO6f7vwIKHdHvjmCgulTNXXy6IAE09Bo5+dISRnRi2WWtZ52jqGu+QXbMr4j50CQC7LkvJZl/Oq0+ErjIRhfCE3O63o2+3KMeJEH9dk1/GSaEivIueq/FM+ryG9JNZy985kQisQKBgQDBPv+0RXU/fPrD2d31QlQ9Ks5yy+vP+A1KrdmF4LVZaUp1UApT6n20Di5ZvRTykZOnYwzO+7oUezB8gMYRlvIyFAB3IKpoTq0JulcCUKguKvMIR5OuLhh/VSuS63XpEj7/DNwzgPvJoivAbEkjz3KCXtTULqeBVIKS65ffum3sCQKBgQC5OImpLeZOTVcwhqBRwBL+sWfe+s4FrOmCstA3bhaMOrYRCQ47wMObJdyIClhpDQLdu/2OIwPzClV/Teoy8Jh0VNWyJqZZobLwtqhcEiZkTdKaf18EZa4Dp7S2t8k2kktnmAqZwxgSK5b5Iwz8ATGwKq5ljEMiXpaqZXVzeteWfwKBgB90DJSf9YyG4B3+mRTxXdqy2/VtYY1xF2C/WTdkMsBB1f+wLm4oLZuf70Vl9EHjFgBZq90Itendfy/UmeiFBBjIjqbVJ0dg/uMldEIJNMaG8RW46L2h/RTEnRoFvyRXdu92fWTYqqLQBYUFWwY8Hqw/mS6MBXGUeshFFmww4PFZAoGAP/5juZsNLlKUZESvUW9uYKcEv0qiMd3LFAgZ3nzft3V1gC9DxqtUKd+L8Ui5wRnoupmAAkLg03Dnl0NhPlZYJiFFb/bglKlEfNxvENiIgTFOKWkGeCt1Uvf3fQXEtGEZQmKKjxOT1ntO/yb4Dn2PIFGuzZEHhPFvVtIVIV/46psCgYEAmIH1HV13Km67KeZXs2GfVYPQAomS9qqjpdHL/+D+dRwMFsSLwK0YduNEaG7IKbw7scZZR8Oh6W60KsIUNvEB6dTC5mryTV37XR2EEEehWOXur126xGDz5/0MQZznZWdni5MENQUFZYukUhCCGrSSRU6wuZpgebLOyASkpNX7wMo=";
    // 4 数据格式:json
    public static String format = "JSON";
    //5 编码格式:utf-8
    public static String charset = "utf-8";
    // 6 阿里公钥
    public static String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA48kjQuKTk+Q7+CGXAqfEMwl24I6dzr6h+eXF/FtLWbF26OWTYRc864HMlSlTnPNjmcpICXc8Xc6ZPkntSogcGhty9Rw9ER31oZ6moYLC5v2gnuO2u4gL0MU9HvEVZU+Dy7I6zzHPowxD1DWLgPqwU2zUGku61omut0+E43cvTkBb2bq+U+1hL63xUrkDo0X1jNctcxgpsPRCfomGY5f51YzyXfZmf6uDnmpZAUrYqX8l+wcOSZujJXFfvH7cCH1ezdOmTXmFAfGODtckwSv8yNt5sX1v6V0uRCLDzS2HuTxdGanbs4K/uq5X06KX1NhxJYLlL6IY9xGcODT0aIglIwIDAQAB";
    // 7 签名方式:rsa2
    public static String signType = "RSA2";

    @Autowired
    RabbitService rabbitService;

    @SneakyThrows
    @Override
    public String submitAlipay(String orderNo) {

        // orderNo查询订单信息
        R<OrderInfo> orderInfoR =  remoteOrderService.getOrderInfoByOrderNo(orderNo,"inner");
        OrderInfo orderInfo = orderInfoR.getData();
        List<OrderItem> orderItemList = orderInfo.getOrderItems();
//
        // 封装支付宝请求对象
        DefaultAlipayClient defaultAlipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);

        // 封装支付宝请求参数对象
        AlipayTradeWapPayRequest alipayTradeWapPayRequest = new AlipayTradeWapPayRequest();
        Map<String,Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no",orderInfo.getOrderNo());
        bizContent.put("total_amount",orderInfo.getTotalAmount());
        bizContent.put("subject",orderInfo.getOrderItems().get(0).getSkuName());
        bizContent.put("product_code","QUICK_WAP_WAY");
        alipayTradeWapPayRequest.setBizContent(JSON.toJSONString(bizContent));
        alipayTradeWapPayRequest.setNotifyUrl("http://hxha2p.natappfree.cc/payment/alipay/aliNotify");// 异步通知地址
        alipayTradeWapPayRequest.setReturnUrl("http://192.168.123.99/#/pages/money/paySuccess");// 同步跳转体制

        // 携带参数发送请求
        AlipayTradeWapPayResponse alipayTradeWapPayResponse = defaultAlipayClient.pageExecute(alipayTradeWapPayRequest);
        String form = alipayTradeWapPayResponse.getBody();
        System.out.println(form);

        System.out.println("保存支付信息");
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setAmount(orderInfo.getTotalAmount());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPayType(2L);
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setPaymentStatus("0");
        paymentInfoMapper.insert(paymentInfo);

        return form;
    }

    @Override
    public void aliNotify(Map<String, String> paramMap) {
        String out_trade_no = paramMap.get("out_trade_no");
        System.out.println("支付系统业务更新");
        System.out.println("通知订单系统更新订单信息");
        rabbitService.sendMessage(MqConst.EXCHANGE_PAYMENT_PAY,MqConst.ROUTING_PAYMENT_PAY,out_trade_no);
        System.out.println("通知库存系统扣减库存");
        rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT,MqConst.ROUTING_MINUS,out_trade_no);
        System.out.println("修改支付状态");
        PaymentInfo payStatus = getPayStatus(out_trade_no);
        payStatus.setPaymentStatus("1");
        paymentInfoMapper.updateById(payStatus);
    }

    @Override
    public PaymentInfo getPayStatus(String out_trade_no) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, out_trade_no));

        System.out.println("查询支付状态");
        return paymentInfo;
    }
}
