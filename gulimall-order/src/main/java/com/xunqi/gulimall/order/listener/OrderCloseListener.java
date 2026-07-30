package com.xunqi.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.xunqi.gulimall.order.entity.OrderEntity;
import com.xunqi.gulimall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 关单监听器（消费 order.release.order.queue）。
 * 订单创建后进入延迟队列，超时未支付由死信触发此监听，调用 orderService.closeOrder() 关单；
 * 若关闭时订单仍为“待付款”则置为“已取消”并发 order.release.other 事件，进而释放库存。
 * 采用手动 ACK：成功 basicAck，异常 basicReject(requeue=true) 重试。
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

}
