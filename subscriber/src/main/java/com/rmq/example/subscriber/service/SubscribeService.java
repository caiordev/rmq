package com.rmq.example.subscriber.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmq.example.subscriber.model.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeService.class);

    @Autowired
    ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queueName}")
    public void receiveMessage(String messageJson) {
        logger.info("receive message: {}", messageJson);
        try {
            QueueMessage message = objectMapper.readValue(messageJson, QueueMessage.class);
            logger.info("Message deserialized successfully: {}", message.toString());

        } catch (JsonProcessingException e) {
            logger.error("Error processing message: {}", messageJson, e);
        }

    }


}
