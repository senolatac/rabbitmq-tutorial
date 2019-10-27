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
public class TTLConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.ttl.queue}")
    private String queueName;

    @Bean
    Queue createTTLQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 5000);
        return new Queue(queueName, true, false, false, args);
    }

    //Important: Each bean name should be unique in application.
    @Bean
    public AmqpTemplate defaultTTLQueue(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        //In default queue, routing key = queue name
        rabbitTemplate.setRoutingKey(queueName);
        return rabbitTemplate;
    }

    //In a managed bean, @PostConstruct is called only once after the regular Java object constructor.
    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(createTTLQueue());
    }
}
