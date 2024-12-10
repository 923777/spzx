package com.spzx.common.rabbit.config;

import com.alibaba.fastjson2.JSON;
import com.spzx.common.rabbit.entity.GuiguCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitInitConfigApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.setupCallbacks();
    }

    private void setupCallbacks() {

        /**
         * 只确认消息是否正确到达 Exchange 中,成功与否都会回调
         *
         * @param correlation 相关数据  非消息本身业务数据
         * @param ack             应答结果
         * @param reason           如果发送消息到交换器失败，错误原因
         */
        this.rabbitTemplate.setConfirmCallback((correlationData, ack, reason) -> {
            if (ack) {

                //消息到交换器成功
                log.info("消息发送到Exchange成功：{}", correlationData);
            } else {
                //消息到交换器失败
                log.error("消息发送到Exchange失败：{}", reason);
                retrySendMsg( (GuiguCorrelationData)correlationData);
            }
        });

        /**
         * 消息没有正确到达队列时触发回调，如果正确到达队列不执行
         */
        this.rabbitTemplate.setReturnsCallback(returned -> {
            log.error("Returned: " + returned.getMessage() + "\nreplyCode: " + returned.getReplyCode()
                    + "\nreplyText: " + returned.getReplyText() + "\nexchange/rk: "
                    + returned.getExchange() + "/" + returned.getRoutingKey());
            String rediskey = returned.getMessage().getMessageProperties().getHeader("spring_returned_message_correlation");
            String s = redisTemplate.opsForValue().get(rediskey);
            GuiguCorrelationData guiguCorrelationData = JSON.parseObject(s, GuiguCorrelationData.class);
            boolean delay = guiguCorrelationData.isDelay();
            if (!delay) {
                retrySendMsg(guiguCorrelationData);
            }


        });
    }
    public void retrySendMsg(GuiguCorrelationData guiguCorrelationData) {
        System.out.println("尝试重发消息");
        String exchange = guiguCorrelationData.getExchange();
        int retryCount = guiguCorrelationData.getRetryCount();
        String routingKey = guiguCorrelationData.getRoutingKey();
        Object message = guiguCorrelationData.getMessage();
        if (retryCount > 0) {
            System.out.println("剩余重试次数：" + retryCount);
            retryCount--;
            guiguCorrelationData.setRetryCount(retryCount);
            redisTemplate.opsForValue().set(guiguCorrelationData.getId(), JSON.toJSONString(guiguCorrelationData));
            rabbitTemplate.convertAndSend(exchange, routingKey,message, guiguCorrelationData);
        }else {
            System.out.println("消息重试次数已用完");
        }

    }

}