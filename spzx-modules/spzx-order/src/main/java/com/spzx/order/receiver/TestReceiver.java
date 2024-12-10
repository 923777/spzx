package com.spzx.order.receiver;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.spzx.common.core.utils.uuid.UUID;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.rabbit.config.DeadLetterMqConfig;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.entity.GuiguCorrelationData;
import com.spzx.order.configure.DelayedMqConfig;
import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

import static com.spzx.common.core.web.domain.AjaxResult.success;


@Slf4j
@Component
public class TestReceiver {
    private  RedisTemplate<String, String> redisTemplate;
    @RabbitListener(bindings =@QueueBinding(exchange = @Exchange(value = MqConst.EXCHANGE_TEST, durable = "true", type = "direct"),
            value = @Queue(value = MqConst.QUEUE_TEST,durable = "true"),
            key=MqConst.ROUTING_TEST))
    public void receive(Channel channel, Message message, String msg) throws IOException {
        System.out.println("receive message:"+msg);
        log.info("receive message:{}",message);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
    @RabbitListener(bindings =@QueueBinding(exchange =@Exchange(value= MqConst.QUEUE_CONFIRM,durable = "true",type = "direct"),
            value =@Queue(value = MqConst.QUEUE_CONFIRM,durable = "true"),
            key=MqConst.ROUTING_CONFIRM))
    public void confirm(Channel channel, Message message, String msg) throws IOException {
        System.out.println("receive message:"+msg);
        log.info("receive message:{}",message);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @SneakyThrows
    @RabbitListener(queues = {DeadLetterMqConfig.queue_dead_2})
    public void getDeadLetterMsg(String msg, Message message, Channel channel) {
        log.info("死信消费者：{}", msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DelayedMqConfig.queue_delay_1)
    public void c(Channel channel , Message message , String msg) throws IOException {

        System.out.println("测试延迟消息队列。。。");
        System.out.println(msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
