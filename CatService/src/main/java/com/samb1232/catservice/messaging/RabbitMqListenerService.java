package com.samb1232.catservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samb1232.catservice.database.DBService;
import com.samb1232.catservice.database.entities.Cat;
import com.samb1232.catservice.dto.AddCatMessage;
import com.samb1232.catservice.dto.TGUser;

@Service
public class RabbitMqListenerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqListenerService.class);

    private final DBService dbService;
    private final ObjectMapper objectMapper;

    public RabbitMqListenerService(DBService dbService) {
        this.dbService = dbService;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = "${app.rabbitmq.queue:telegram-service-queue}")
    public void receiveMessage(String message) {
        LOGGER.info("Received message from queue: {}", message);

        try {
            AddCatMessage addCatMessage = objectMapper.readValue(message, AddCatMessage.class);

            LOGGER.info("Processing AddCatMessage - userId: {}, catName: {}, photoFileId: {}",
                    addCatMessage.getUserId(),
                    addCatMessage.getCatName(),
                    addCatMessage.getPhotoFileId());

            // Ensure user exists in database
            TGUser tgUser = new TGUser(
                    addCatMessage.getUserId(),
                    null,
                    addCatMessage.getCatName() != null ? addCatMessage.getCatName() : "Unknown",
                    null
            );
            dbService.getOrCreateUser(tgUser);

            Cat cat = dbService.createCat(
                    addCatMessage.getUserId(),
                    addCatMessage.getPhotoFileId()
            );

            LOGGER.info("Successfully created cat with ID: {} for user: {}",
                    cat.getCatId(),
                    addCatMessage.getUserId());

        } catch (Exception e) {
            LOGGER.error("Failed to process message: {}", message, e);
        }
    }
}
