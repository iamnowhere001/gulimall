package com.xunqi.gulimall.ware.listener;

import com.rabbitmq.client.Channel;
import com.xunqi.common.to.OrderTo;
import com.xunqi.common.to.mq.StockLockedTo;
import com.xunqi.gulimall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 库存释放监听器（消费 stock.release.stock.queue）。
 *
 * 提供两类解锁处理：
 *  - handleStockLockedRelease(StockLockedTo)：下单锁定库存后进入延迟队列，超时触发，
 *    按库存工作单明细 + 订单状态判断是否需要回退库存（订单已取消才解锁）；
 *  - handleOrderCloseRelease(OrderTo)：订单服务主动关闭订单时发来的解锁消息，直接按工作单解锁。
 * 采用手动 ACK：解锁成功才 basicAck，失败则 basicReject(requeue=true) 重新入队由其它消费者重试，
 * 确保库存不会因消息丢失而漏解锁。
 */
@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
@Service
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 1、库存自动解锁
     *  下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁定的库存就要自动解锁
     *
     *  2、订单失败
     *      库存锁定失败
     *
     *   只要解锁库存的消息失败，一定要告诉服务解锁失败
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        log.info("******收到解锁库存的信息******");
        try {

            //解锁库存
            wareSkuService.unlockStock(to);
            // 手动删除消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            // 解锁失败 将消息重新放回队列，让别人消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {

        log.info("******收到订单关闭，准备解锁库存的信息******");

        try {
            wareSkuService.unlockStock(orderTo);
            // 手动删除消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            // 解锁失败 将消息重新放回队列，让别人消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}
