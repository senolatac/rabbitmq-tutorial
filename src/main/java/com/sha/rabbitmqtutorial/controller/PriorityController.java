package com.sha.rabbitmqtutorial.controller;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class PriorityController {

    @Autowired
    private AmqpTemplate defaultPriorityQueue;

    @GetMapping("/priority")
    public String sendPriorityMessage(@RequestParam(value = "priority", required = false, defaultValue = "0") int priority) {
        QueueObject object = new QueueObject("Default Priority Queue Priority: " + priority , LocalDateTime.now());
        defaultPriorityQueue.convertSendAndReceive(object, (message) -> {
            message.getMessageProperties().setPriority(priority);
            return message;
        });
        return "Priority message is sent...";
    }
}
