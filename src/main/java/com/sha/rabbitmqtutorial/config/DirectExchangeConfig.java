package com.sha.rabbitmqtutorial.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DirectExchangeConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.direct.queue.1}")
    private String directQueue1;

    @Value("${rabbitmq.direct.queue.2}")
    private String directQueue2;

    @Value("${rabbitmq.direct.exchage}")
    private String directExchange;

    @Value("${rabbitmq.direct-routing-key.1}")
    private String directRoutingKey1;

    @Value("${rabbitmq.direct-routing-key.2}")
    private String directRoutingKey2;

    @Bean
    Queue createDirectQueue1() {
        return new Queue(directQueue1, true, false, false);
    }

    @Bean
    Queue createDirectQueue2() {
        return new Queue(directQueue2, true, false, false);
    }

    @Bean
    DirectExchange createDirectExchange() {
        return new DirectExchange(directExchange, true, false);
    }

    @Bean
    Binding createDirectBinding1() {
        return BindingBuilder.bind(createDirectQueue1()).to(createDirectExchange()).with(directRoutingKey1);
    }

    @Bean
    Binding createDirectBinding2() {
        return BindingBuilder.bind(createDirectQueue2()).to(createDirectExchange()).with(directRoutingKey2);
    }

    @Bean
    public AmqpTemplate directExchange(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(directExchange);
        return rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(createDirectQueue1());
        amqpAdmin.declareQueue(createDirectQueue2());
        amqpAdmin.declareExchange(createDirectExchange());
        amqpAdmin.declareBinding(createDirectBinding1());
        amqpAdmin.declareBinding(createDirectBinding2());
    }
}
