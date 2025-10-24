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
    private final String queueName;
    private final String getMyCatsQueueName;
    private final String deleteCatRequestQueueName;

    public RabbitMqService(RabbitTemplate rabbitTemplate,
                           @Value("${app.rabbitmq.add_cat_request_queue}") String queueName,
                           @Value("${app.rabbitmq.get_my_cats_queue}") String getMyCatsQueueName,
                           @Value("${app.rabbitmq.delete_cat_request_queue}") String deleteCatRequestQueueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
        this.getMyCatsQueueName = getMyCatsQueueName;
        this.deleteCatRequestQueueName = deleteCatRequestQueueName;
    }

    public void sendToQueue(String message) {
        sendToQueue(queueName, message);
    }

    public void sendToGetMyCatsQueue(String message) {
        sendToQueue(getMyCatsQueueName, message);
    }

    public void sendToDeleteCatRequestQueue(String message) {
        sendToQueue(deleteCatRequestQueueName, message);
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


