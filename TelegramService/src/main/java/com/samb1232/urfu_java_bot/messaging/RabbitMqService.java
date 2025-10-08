package com.samb1232.urfu_java_bot.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqService.class);

    private final RabbitTemplate rabbitTemplate;
    private final String defaultQueueName;

    public RabbitMqService(RabbitTemplate rabbitTemplate,
                           @Value("${app.rabbitmq.queue:telegram-service-queue}") String defaultQueueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.defaultQueueName = defaultQueueName;
    }

    public void sendToQueue(String message) {
        sendToQueue(defaultQueueName, message);
    }

    public void sendToQueue(String queueName, String message) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
            LOGGER.info("Message sent to queue '{}': {}", queueName, message);
        } catch (Exception e) {
            LOGGER.error("Failed to send message to queue '{}': {}", queueName, message, e);
        }
    }
}


