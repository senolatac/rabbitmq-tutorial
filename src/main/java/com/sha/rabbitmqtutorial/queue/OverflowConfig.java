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
public class OverflowConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.overflow.queue}")
    private String queueName;

    @Bean
    Queue createOverflowQueue() {
        Map<String, Object> args = new HashMap<>();
        //To use overflow arg, first of all it should be occurred overflow.
        args.put("x-max-length", 2);
        //drop-head(default) or reject-publish.
        //drop-head: Ex: queue: (1.test1, 2.test2, 3.test3) -> publish(test4) -> Result: (1.test2, 2.test3, 3.test4)
        //reject-publish: Ex: queue: (1.test1, 2.test2, 3.test3) -> publish(test4) -> Result: (1.test1, 2.test2, 3.test3)
        args.put("x-overflow", "reject-publish");
        return new Queue(queueName, true, false, false, args);
    }

    //Important: Each bean name should be unique in application.
    @Bean
    public AmqpTemplate defaultOverflowQueue(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        //In default queue, routing key = queue name
        rabbitTemplate.setRoutingKey(queueName);
        return rabbitTemplate;
    }

    //In a managed bean, @PostConstruct is called only once after the regular Java object constructor.
    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(createOverflowQueue());
    }
}
