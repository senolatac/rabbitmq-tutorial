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
public class FanoutExchangeConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.fanout.queue.1}")
    private String fanoutQueue1;

    @Value("${rabbitmq.fanout.queue.2}")
    private String fanoutQueue2;

    @Value("${rabbitmq.fanout-exchange}")
    private String fanoutExchange;

    @Bean
    Queue createFanoutQueue1() {
        return new Queue(fanoutQueue1, true, false, false);
    }

    @Bean
    Queue createFanoutQueue2() {
        return new Queue(fanoutQueue2, true, false, false);
    }

    @Bean
    FanoutExchange createFanoutExchange() {
        return new FanoutExchange(fanoutExchange, true, false);
    }

    @Bean
    Binding createFanoutBinding1() {
        return BindingBuilder.bind(createFanoutQueue1()).to(createFanoutExchange());
    }

    @Bean
    Binding createFanoutBinding2() {
        return BindingBuilder.bind(createFanoutQueue2()).to(createFanoutExchange());
    }

    @Bean
    public AmqpTemplate fanoutExchange(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(fanoutExchange);
        return rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(createFanoutQueue1());
        amqpAdmin.declareQueue(createFanoutQueue2());
        amqpAdmin.declareExchange(createFanoutExchange());
        amqpAdmin.declareBinding(createFanoutBinding1());
        amqpAdmin.declareBinding(createFanoutBinding2());
    }
}
