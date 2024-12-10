package com.spzx.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.payment.domain.PaymentInfo;

import java.util.Map;

public interface AlipayService extends IService<PaymentInfo> {
    String submitAlipay(String orderNo);

    void aliNotify(Map<String, String> paramMap);

    PaymentInfo getPayStatus(String out_trade_no);
}
