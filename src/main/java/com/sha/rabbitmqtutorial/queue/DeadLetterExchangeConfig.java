package com.sha.rabbitmqtutorial.queue;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeadLetterExchangeConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.dead-letter-exchange-queue}")
    private String queueName;

    @Value("${rabbitmq.fanout-exchange}")
    private String fanoutExchange;

    @Bean
    Queue createDeadLetterExchangeQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 5000);
        //When messages are expired, they will be redirected to dead-letter-exchange.
        //In here, we used exist predefined fanout exchange. When message is rejected, it will be redirected to:
        // -> fanout-queue-1, fanout-queue-2
        args.put("x-dead-letter-exchange", fanoutExchange);
        return new Queue(queueName, true, false, false, args);
    }

    //Important: Each bean name should be unique in application.
    @Bean
    public AmqpTemplate defaultDeadLetterQueue(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        //In default queue, routing key = queue name
        rabbitTemplate.setRoutingKey(queueName);
        return rabbitTemplate;
    }

    //In a managed bean, @PostConstruct is called only once after the regular Java object constructor.
    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(createDeadLetterExchangeQueue());
    }
}
