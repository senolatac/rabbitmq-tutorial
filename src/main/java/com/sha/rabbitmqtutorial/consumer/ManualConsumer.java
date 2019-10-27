package com.sha.rabbitmqtutorial.consumer;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

@Service
public class ManualConsumer {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private int getCountOfMessage(String queueName) {
        Properties properties = amqpAdmin.getQueueProperties(queueName);
        return (int) properties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
    }

    public List<QueueObject> receiveMessages(String queueName) {
        List<QueueObject> list = new ArrayList<>();
        int count = getCountOfMessage(queueName);
        IntStream.range(0, count).forEach(i-> list.add((QueueObject) rabbitTemplate.receiveAndConvert(queueName)));
        return list;
    }
}
