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
public class TopicExchangeConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.topic.queue.1}")
    private String queueName1;

    @Value("${rabbitmq.topic.queue.2}")
    private String queueName2;

    @Value("${rabbitmq.topic.queue.3}")
    private String queueName3;

    @Value("${rabbitmq.topic.exchange}")
    private String topicExchange;

    @Value("${rabbitmq.topic.pattern.1}")
    private String topicPattern1;

    @Value("${rabbitmq.topic.pattern.2}")
    private String topicPattern2;

    @Value("${rabbitmq.topic.pattern.3}")
    private String topicPattern3;

    @Bean
    Queue topicQueue1() {
        return new Queue(queueName1, true, false, false);
    }

    @Bean
    Queue topicQueue2() {
        return new Queue(queueName2, true, false, false);
    }

    @Bean
    Queue topicQueue3() {
        return new Queue(queueName3, true, false, false);
    }

    @Bean
    TopicExchange createTopicExchange() {
        return new TopicExchange(topicExchange, true, false);
    }

    //routing key can be sent as; *.topic.*; word.topic.word...
    @Bean
    Binding createTopicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(createTopicExchange()).with(topicPattern1);
    }

    //routing key can be sent as; first.topic.#; first.topic; first.topic.word; ...
    @Bean
    Binding createTopicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(createTopicExchange()).with(topicPattern2);
    }

    //routing key can be sent as; second.topic.*; second.topic.word;...
    @Bean
    Binding createTopicBinding3() {
        return BindingBuilder.bind(topicQueue3()).to(createTopicExchange()).with(topicPattern3);
    }

    @Bean
    public AmqpTemplate topicExchange(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(topicExchange);
        return rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(topicQueue1());
        amqpAdmin.declareQueue(topicQueue2());
        amqpAdmin.declareQueue(topicQueue3());
        amqpAdmin.declareExchange(createTopicExchange());
        amqpAdmin.declareBinding(createTopicBinding1());
        amqpAdmin.declareBinding(createTopicBinding2());
        amqpAdmin.declareBinding(createTopicBinding3());
    }
}
