package com.xunqi.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.xunqi.common.to.mq.SeckillOrderTo;
import com.xunqi.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 秒杀下单监听器（消费 order.seckill.order.queue）。
 * 接收秒杀服务发来的 SeckillOrderTo，调用 orderService.createSeckillOrder() 直接落库创建秒杀订单，
 * 无需走购物车确认/令牌流程。成功后 basicAck，异常 basicReject(requeue=true) 重试。
 */
@Slf4j
@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo orderTo, Channel channel, Message message) throws IOException {

        log.info("准备创建秒杀单的详细信息...");

        try {
            orderService.createSeckillOrder(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

}
