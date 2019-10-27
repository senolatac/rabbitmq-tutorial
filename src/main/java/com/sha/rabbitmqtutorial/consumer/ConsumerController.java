package com.sha.rabbitmqtutorial.consumer;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConsumerController {

    @Autowired
    private ManualConsumer manualConsumer;

    @GetMapping("/consume/{queueName}")
    public List<QueueObject> consume(@PathVariable String queueName) {
        return manualConsumer.receiveMessages(queueName);
    }
}
