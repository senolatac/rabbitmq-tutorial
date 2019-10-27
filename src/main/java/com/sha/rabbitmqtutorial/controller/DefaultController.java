package com.sha.rabbitmqtutorial.controller;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class DefaultController {

    @Autowired
    private AmqpTemplate defaultQueue;

    @GetMapping("/default")
    public void sendDefault() {
        QueueObject object = new QueueObject("default", LocalDateTime.now());
        defaultQueue.convertAndSend(object);
    }
}
