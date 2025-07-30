package com.rmq.example.subscriber.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {

    @RabbitListener(queues = "${rabbitmq.queueName}")
    public void receiveMessage(Message message) {
        System.out.println("Received message: " + message);
    }
}
