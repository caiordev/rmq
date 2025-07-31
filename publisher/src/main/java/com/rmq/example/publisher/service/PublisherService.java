package com.rmq.example.publisher.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmq.example.publisher.model.QueueMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

@Service
public class PublisherService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String text, String queueName) {
        System.out.println("Publishing message: " + text);
        rabbitTemplate.convertAndSend(queueName, text);
    }

    public void publishJson(QueueMessage json, String queueName) {
        try {
            System.out.println("Publishing JSON message: " + json.toString());
            String messageJson = objectMapper.writeValueAsString(json);

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

            Message msg = new Message(messageJson.getBytes(StandardCharsets.UTF_8), messageProperties);

            rabbitTemplate.send(queueName, msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to string", e);
        }
    }

}