package com.xunqi.gulimall.seckill.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 自定义消息转换器，将消息以 JSON 格式（而非 JDK 默认的 Java 序列化）进行序列化/反序列化，
 * 这样其他语言或客户端也能正常消费，并且消息可读性更好。
 */
@Configuration
public class MyRabbitMQConfig {

    /**
     * 注入 Jackson 消息转换器，Spring 会自动用它来转换所有收发消息。
     * @return JSON 格式的消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
