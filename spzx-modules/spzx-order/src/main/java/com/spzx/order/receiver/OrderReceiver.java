package com.spzx.order.receiver;

import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.order.configure.DelayedMqOrderConfig;
import com.spzx.order.service.IOrderInfoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderReceiver {
    @Autowired
    private IOrderInfoService orderService;
    @RabbitListener(queues = DelayedMqOrderConfig.QUEUE_ORDER_CANCEL_DELAY)

    public void cencel(Channel channel, Message message, String traNo) throws IOException {
        orderService.cencel(traNo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        System.out.println("取消订单");

    }    @RabbitListener(bindings =@QueueBinding(exchange = @Exchange(value = MqConst.EXCHANGE_PAYMENT_PAY, durable = "true", type = "direct"),
            value = @Queue(value = MqConst.ROUTING_PAYMENT_PAY,durable = "true"),
            key=MqConst.QUEUE_PAYMENT_PAY))
    public void updateOrderStaue(String orderNo) {
        orderService.updateOrderStaue(orderNo);
        System.out.println("更新订单");
    }
}
