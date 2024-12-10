package com.spzx.common.rabbit.service;

import com.alibaba.fastjson2.JSON;
import com.spzx.common.core.utils.uuid.UUID;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.rabbit.entity.GuiguCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.spzx.common.core.web.domain.AjaxResult.success;

@Service
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
    public AjaxResult sendReTryMessage(String exchange, String routingKey, Object message) {
        GuiguCorrelationData guiguCorrelationData = new GuiguCorrelationData();
        String uuid = "mq"+ UUID.randomUUID().toString().replace("-", "");
        guiguCorrelationData.setId(uuid);
        guiguCorrelationData.setMessage(message);
        guiguCorrelationData.setExchange(exchange);
        guiguCorrelationData.setRoutingKey(routingKey);
//        guiguCorrelationData.set
        guiguCorrelationData.setDelay(false);// 是否延迟
        guiguCorrelationData.setRetryCount(3);
        redisTemplate.opsForValue().set(uuid, JSON.toJSONString(guiguCorrelationData));
        rabbitTemplate.convertAndSend(exchange, routingKey, message, guiguCorrelationData);
        return success();
    }
    public boolean sendDealyMessage(String exchange, String routingKey, Object message, int delayTime) {
        //1.创建自定义相关消息对象-包含业务数据本身，交换器名称，路由键，队列类型，延迟时间,重试次数
        GuiguCorrelationData correlationData = new GuiguCorrelationData();
        String uuid = "mq:" + UUID.randomUUID().toString().replaceAll("-", "");
        correlationData.setId(uuid);
        correlationData.setMessage(message);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        correlationData.setDelay(true);
        correlationData.setDelayTime(delayTime);

        //2.将相关消息封装到发送消息方法中
        rabbitTemplate.convertAndSend(exchange, routingKey, message,message1 -> {
            message1.getMessageProperties().setDelay(delayTime*1000);
            return message1;
        }, correlationData);

        //3.将相关消息存入Redis  Key：UUID  相关消息对象  10 分钟
        redisTemplate.opsForValue().set(uuid, JSON.toJSONString(correlationData), 10, TimeUnit.MINUTES);
        return true;

    }
}
