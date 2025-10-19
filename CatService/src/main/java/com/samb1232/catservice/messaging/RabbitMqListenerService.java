package com.samb1232.catservice.messaging;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samb1232.catservice.database.DBService;
import com.samb1232.catservice.database.entities.Cat;
import com.samb1232.catservice.dto.AddCatMessage;
import com.samb1232.catservice.dto.CatInfo;
import com.samb1232.catservice.dto.GetMyCatsMessage;
import com.samb1232.catservice.dto.MyCatsResponse;
import com.samb1232.catservice.dto.TGUser;

@Service
public class RabbitMqListenerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqListenerService.class);

    private final DBService dbService;
    private final ObjectMapper objectMapper;
    private final RabbitMqSenderService rabbitMqSenderService;

    public RabbitMqListenerService(DBService dbService, RabbitMqSenderService rabbitMqSenderService) {
        this.dbService = dbService;
        this.objectMapper = new ObjectMapper();
        this.rabbitMqSenderService = rabbitMqSenderService;
    }

    @RabbitListener(queues = "${app.rabbitmq.add_cat_request_queue}")
    public void handleAddCatRequestMessage(String message) {
        LOGGER.info("Received message from add cat request queue: {}", message);

        try {
            AddCatMessage addCatMessage = objectMapper.readValue(message, AddCatMessage.class);

            LOGGER.info("Processing AddCatMessage - userId: {}, catName: {}, photoPath: {}",
                    addCatMessage.getUserId(),
                    addCatMessage.getCatName(),
                    addCatMessage.getPhotoPath());

            TGUser tgUser = new TGUser(
                    addCatMessage.getUserId(),
                    null,
                    "User",
                    null
            );
            dbService.getOrCreateUser(tgUser);

            Cat cat = dbService.createCat(
                    addCatMessage.getUserId(),
                    addCatMessage.getPhotoPath(),
                    addCatMessage.getCatName()
            );

            LOGGER.info("Successfully created cat with ID: {} for user: {}",
                    cat.getCatId(),
                    addCatMessage.getUserId());

        } catch (Exception e) {
            LOGGER.error("Failed to process message: {}", message, e);
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.get_my_cats_queue}")
    public void handleGetMyCatsMessage(String message) {
        LOGGER.info("Received message from get my cats queue: {}", message);

        try {
            GetMyCatsMessage getMyCatsMessage = objectMapper.readValue(message, GetMyCatsMessage.class);
            Long userId = getMyCatsMessage.getUserId();

            LOGGER.info("Processing GetMyCatsMessage - userId: {}", userId);

            List<Cat> userCats = dbService.getCatsWithReactionsByUser(userId);

            List<CatInfo> catInfoList = userCats.stream()
                    .map(cat -> {
                        long likes = dbService.getLikesCount(cat.getCatId());
                        long dislikes = dbService.getDislikesCount(cat.getCatId());
                        return new CatInfo(
                                cat.getCatId(),
                                cat.getName(),
                                cat.getPhotoPath(),
                                likes,
                                dislikes
                        );
                    })
                    .collect(Collectors.toList());

            MyCatsResponse response = new MyCatsResponse(userId, catInfoList);
            rabbitMqSenderService.sendToMyCatsResponseQueue(response);

            LOGGER.info("Successfully sent {} cats for user: {}", catInfoList.size(), userId);

        } catch (Exception e) {
            LOGGER.error("Failed to process get my cats message: {}", message, e);
        }
    }
}
