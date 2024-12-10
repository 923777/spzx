package com.spzx.order.configure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayedMqOrderConfig {
    public static final String EXCHANGE_ORDER_CANCEL_DELAY = "exchange.order.cancel.delay";
    public static final String ROUTING_ORDER_CANCEL_DELAY = "routing.order.cancel.delay";
    public static final String QUEUE_ORDER_CANCEL_DELAY = "queue.order.cancel.delay";
    @Bean
    public Queue delayQeue1() {
        // 第一个参数是创建的queue的名字，第二个参数是是否支持持久化
        return new Queue(QUEUE_ORDER_CANCEL_DELAY, true);
    }

    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(EXCHANGE_ORDER_CANCEL_DELAY, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding delayBbinding1() {
        return BindingBuilder.bind(delayQeue1()).to(delayExchange()).with(ROUTING_ORDER_CANCEL_DELAY).noargs();
    }



}



