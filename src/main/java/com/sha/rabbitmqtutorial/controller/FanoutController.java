package com.sha.rabbitmqtutorial.controller;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class FanoutController {

    @Autowired
    private AmqpTemplate fanoutExchange;

    @GetMapping("/fanout")
    public String sendFanout() {
        QueueObject object = new QueueObject("fanout", LocalDateTime.now());
        fanoutExchange.convertAndSend(object);
        return "Message was sent to fanout...";
    }
}
