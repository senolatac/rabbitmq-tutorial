package com.sha.rabbitmqtutorial.controller;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class DirectController {

    @Autowired
    private AmqpTemplate directExchange;

    @Value("${rabbitmq.direct-routing-key.1}")
    private String directRoutingKey1;

    @Value("${rabbitmq.direct-routing-key.2}")
    private String directRoutingKey2;

    @GetMapping("/direct/{key}")
    public String sendDirect(@PathVariable int key) {
        QueueObject object = new QueueObject("Direct", LocalDateTime.now());
        directExchange.convertAndSend(key == 1 ? directRoutingKey1 : directRoutingKey2, object);
        return "Direct message was sent...";
    }
}
