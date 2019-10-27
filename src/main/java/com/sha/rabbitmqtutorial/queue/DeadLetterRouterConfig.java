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
public class DeadLetterRouterConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.dead-letter-router.queue}")
    private String queueName;

    @Value("${rabbitmq.direct.exchage}")
    private String directExchange;

    @Value("${rabbitmq.direct-routing-key.1}")
    private String directRoutingKey1;

    @Bean
    Queue createDeadLetterRouterQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 5000);
        //When messages are expired, they will be redirected to dead-letter-routing-key.
        //In here, we used exist predefined direct exchange. When message is rejected, it will be redirected to:
        // -> direct-queue-1
        args.put("x-dead-letter-exchange", directExchange);
        //Exchange is also required for dead-letter-roting-key.
        args.put("x-dead-letter-routing-key", directRoutingKey1);
        return new Queue(queueName, true, false, false, args);
    }

    //Important: Each bean name should be unique in application.
    @Bean
    public AmqpTemplate defaultDeadLetterRouterQueue(ConnectionFactory connectionFactory,
                                                MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        //In default queue, routing key = queue name
        rabbitTemplate.setRoutingKey(queueName);
        return rabbitTemplate;
    }

    //In a managed bean, @PostConstruct is called only once after the regular Java object constructor.
    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(createDeadLetterRouterQueue());
    }
}
