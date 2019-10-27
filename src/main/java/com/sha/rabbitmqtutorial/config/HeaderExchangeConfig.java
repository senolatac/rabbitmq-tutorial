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
public class HeaderExchangeConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.header.queue.1}")
    private String queueName1;

    @Value("${rabbitmq.header.queue.2}")
    private String queueName2;

    @Value("${rabbitmq.header.exchange}")
    private String headerExchange;

    @Bean
    Queue createHeaderQueue1() {
        return new Queue(queueName1, true, false, false);
    }

    @Bean
    Queue createHeaderQueue2() {
        return new Queue(queueName2, true, false, false);
    }

    @Bean
    HeadersExchange createHeaderExchange() {
        return new HeadersExchange(headerExchange, true, false);
    }

    //To accept it; error and debug (both of them) should be contained.
    @Bean
    Binding createHeaderBinding1() {
        return BindingBuilder.bind(createHeaderQueue1()).to(createHeaderExchange()).whereAll("error", "debug").exist();
    }

    //To accept it; info or warning (one of them) will be enough.
    @Bean
    Binding createHeaderBinding2() {
        return BindingBuilder.bind(createHeaderQueue2()).to(createHeaderExchange()).whereAny("info", "warning").exist();
    }

    @Bean
    public AmqpTemplate headerExchange(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(headerExchange);
        return rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(createHeaderQueue1());
        amqpAdmin.declareQueue(createHeaderQueue2());
        amqpAdmin.declareExchange(createHeaderExchange());
        amqpAdmin.declareBinding(createHeaderBinding1());
        amqpAdmin.declareBinding(createHeaderBinding2());
    }
}
