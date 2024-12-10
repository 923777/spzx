package com.spzx.order.controller;

import com.alibaba.fastjson.JSON;
import com.spzx.common.core.utils.uuid.UUID;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.rabbit.config.DeadLetterMqConfig;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.entity.GuiguCorrelationData;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.configure.DelayedMqConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "Mq接口管理")
@RestController
@RequestMapping("/mq")
public class MqController extends BaseController   {
@Autowired
    RabbitService rabbitService;
@Autowired
    RedisTemplate<String, String> redisTemplate;
@GetMapping("/sendMessage")
@Operation(summary = "发送消息")
public AjaxResult sendMessage() {
      rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_TEST, "测试消息");
    System.out.println("发送消息成功");

    return success();
    }

    @GetMapping("/sendConfirmMessage")
    @Operation(summary = "发送消息")
    public AjaxResult sendConfirmMessage() {
        rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_CONFIRM, "确认消息");
        System.out.println("发送消息成功");
        return success();
    }
    @GetMapping("/sendRetryMessage")
    @Operation(summary = "发送消息")
    public AjaxResult sendRetryMessage() {
        rabbitService.sendReTryMessage("MqConst.EXCHANGE_TEST", MqConst.ROUTING_CONFIRM ,"确认消息");
        System.out.println("发送消息成功");
        return success();
    }

    @Operation(summary = "发送延迟消息：基于死信实现")
    @GetMapping("/sendDeadLetterMsg")
    public AjaxResult sendDeadLetterMsg() {
        rabbitService.sendMessage(DeadLetterMqConfig.exchange_dead, DeadLetterMqConfig.routing_dead_1, "我是延迟消息");
        return success();
    }
    @Operation(summary = "发送延迟消息：基于延迟插件")
    @GetMapping("/sendDelayMsg")
    public AjaxResult sendDelayMsg() {
        //调用工具方法发送延迟消息
        int delayTime = 10;
        rabbitService.sendDealyMessage(DelayedMqConfig.exchange_delay, DelayedMqConfig.routing_delay, "我是延迟消息", delayTime);
        return success();
    }


}
