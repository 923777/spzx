package com.spzx.payment.controller;


import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("alipay")
public class AlipayController extends BaseController {

    @Autowired
    AlipayService alipayService;

    @GetMapping("submitAlipay/{orderNo}")
    public AjaxResult submitAlipay(@PathVariable String orderNo){
        String form = alipayService.submitAlipay(orderNo);//"支付宝的跳转html页面";
        return success(form);
    }

    @PostMapping("/aliNotify")
    public String aliNotify(HttpServletRequest request,@RequestParam Map<String, String> paramMap){
        // 幂等性校验
        // TODO
        String out_trade_no = (String)paramMap.get("out_trade_no");
        PaymentInfo PaymentStatus = alipayService.getPayStatus(out_trade_no);
        if(PaymentStatus.equals("2")){
            return "success";
        }
        System.out.println("接到支付宝回调通知，更新系统");
        alipayService.aliNotify(paramMap);
        return "success";
    }
}
