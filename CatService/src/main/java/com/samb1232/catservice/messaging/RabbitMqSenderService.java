package com.samb1232.catservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqSenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqSenderService.class);

    private final RabbitTemplate rabbitTemplate;
    private final String myCatsResponseQueueName;
    private final String viewRandomCatResponseQueueName;

    public RabbitMqSenderService(RabbitTemplate rabbitTemplate,
                                 @Value("${app.rabbitmq.my_cats_response_queue}") String myCatsResponseQueueName,
                                 @Value("${app.rabbitmq.view_random_cat_response_queue}") String viewRandomCatResponseQueueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.myCatsResponseQueueName = myCatsResponseQueueName;
        this.viewRandomCatResponseQueueName = viewRandomCatResponseQueueName;
    }

    public void sendToMyCatsResponseQueue(Object message) {
        try {
            rabbitTemplate.convertAndSend(myCatsResponseQueueName, message);
            LOGGER.info("Message sent to queue '{}': {}", myCatsResponseQueueName, message);
        } catch (Exception e) {
            LOGGER.error("Failed to send message to queue '{}': {}", myCatsResponseQueueName, message, e);
        }
    }

    public void sendToViewRandomCatResponseQueue(Object message) {
        try {
            rabbitTemplate.convertAndSend(viewRandomCatResponseQueueName, message);
            LOGGER.info("Message sent to queue '{}': {}", viewRandomCatResponseQueueName, message);
        } catch (Exception e) {
            LOGGER.error("Failed to send message to queue '{}': {}", viewRandomCatResponseQueueName, message, e);
        }
    }
}
