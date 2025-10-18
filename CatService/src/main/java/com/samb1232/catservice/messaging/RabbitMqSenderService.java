package com.samb1232.catservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RabbitMqSenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqSenderService.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String myCatsResponseQueueName;

    public RabbitMqSenderService(RabbitTemplate rabbitTemplate,
                                 @Value("${app.rabbitmq.my_cats_response_queue}") String myCatsResponseQueueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.myCatsResponseQueueName = myCatsResponseQueueName;
    }

    public void sendToMyCatsResponseQueue(Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(myCatsResponseQueueName, jsonMessage);
            LOGGER.info("Message sent to queue '{}': {}", myCatsResponseQueueName, jsonMessage);
        } catch (Exception e) {
            LOGGER.error("Failed to send message to queue '{}': {}", myCatsResponseQueueName, message, e);
        }
    }
}
