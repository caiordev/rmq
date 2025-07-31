package com.rmq.example.publisher.controller;

import com.rmq.example.publisher.model.QueueMessage;
import com.rmq.example.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Publisher {

    @Value("${rabbitmq.queueName}")
    private String queueName;

    @Autowired
    private PublisherService publisherService;

    @PostMapping("/publish")
    public void publishText(@RequestBody String text) {
        publisherService.publish(text, queueName);
    }

    @PostMapping("/publish/json")
    public void publishJson(@RequestBody QueueMessage json) {
        publisherService.publishJson(json, queueName);
    }
}
