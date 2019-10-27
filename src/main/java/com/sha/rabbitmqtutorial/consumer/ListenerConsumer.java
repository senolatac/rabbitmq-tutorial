package com.sha.rabbitmqtutorial.consumer;

import com.sha.rabbitmqtutorial.model.QueueObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerConsumer {

    //It expects QueueObject but we published String so we got error. To handle it, we've created generic object as
    // expected parameter.
    @RabbitListener(queues = {"${rabbitmq.direct.queue.1}","${rabbitmq.direct.queue.2}"}, containerFactory = "createListener")
    public void receiveMessages(Object object) {
        System.out.println(object);
    }
}
