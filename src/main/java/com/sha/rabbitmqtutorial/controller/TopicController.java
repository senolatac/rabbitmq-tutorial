package com.sha.rabbitmqtutorial.controller;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class TopicController {

    @Autowired
    private AmqpTemplate topicExchange;

    @GetMapping("/topic/{key}")
    public String sendTopic(@PathVariable String key) {
        QueueObject object = new QueueObject("topic", LocalDateTime.now());
        topicExchange.convertAndSend(key, object);
        return "Message is sent to topic...";
    }
}
