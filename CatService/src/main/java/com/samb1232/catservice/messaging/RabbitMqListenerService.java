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
import com.samb1232.catservice.database.entities.CatReaction;
import com.samb1232.common.dto.AddCatMessage;
import com.samb1232.common.dto.CatInfo;
import com.samb1232.common.dto.DeleteCatMessage;
import com.samb1232.common.dto.GetMyCatsMessage;
import com.samb1232.common.dto.MyCatsResponse;
import com.samb1232.common.dto.SetCatReactionMessage;
import com.samb1232.common.dto.TGUser;
import com.samb1232.common.dto.ViewRandomCatRequestMessage;
import com.samb1232.common.dto.ViewRandomCatResponseMessage;

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
                        int likes = dbService.getLikesCount(cat.getCatId());
                        int dislikes = dbService.getDislikesCount(cat.getCatId());
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

    @RabbitListener(queues = "${app.rabbitmq.delete_cat_request_queue}")
    public void handleDeleteCatRequest(String message) {
        LOGGER.info("Received message from delete cat request queue: {}", message);

        try {
            DeleteCatMessage deleteCatMessage = objectMapper.readValue(message, DeleteCatMessage.class);
            Long catId = deleteCatMessage.getCatId();

            LOGGER.info("Processing DeleteCatRequest - catId: {}", catId);

            dbService.deleteCat(catId);

            LOGGER.info("Successfully deleted cat with ID: {}", catId);

        } catch (Exception e) {
            LOGGER.error("Failed to process delete cat request: {}", message, e);
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.view_random_cat_request_queue}")
    public void handleViewRandomCatRequest(String message) {
        LOGGER.info("Received message from view random cat request queue: {}", message);

        try {
            ViewRandomCatRequestMessage viewRandomCatRequestMessage = objectMapper.readValue(message, ViewRandomCatRequestMessage.class);
            Long userId = viewRandomCatRequestMessage.getUserId();

            LOGGER.info("Processing ViewRandomCatRequest - userId: {}", userId);

            dbService.getRandomCat().ifPresentOrElse(
                cat -> {
                    int likes = dbService.getLikesCount(cat.getCatId());
                    int dislikes = dbService.getDislikesCount(cat.getCatId());

                    ViewRandomCatResponseMessage response = new ViewRandomCatResponseMessage(
                        userId,
                        cat.getCatId(),
                        cat.getName(),
                        cat.getPhotoPath(),
                        likes,
                        dislikes
                    );

                    rabbitMqSenderService.sendToViewRandomCatResponseQueue(response);
                    LOGGER.info("Successfully sent random cat (ID: {}) for user: {}", cat.getCatId(), userId);
                },
                () -> {
                    LOGGER.warn("No cats available in database for user: {}", userId);
                }
            );

        } catch (Exception e) {
            LOGGER.error("Failed to process view random cat request: {}", message, e);
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.set_cat_reaction_queue}")
    public void handleSetCatReaction(String message) {
        LOGGER.info("Received message from set cat reaction queue: {}", message);

        try {
            SetCatReactionMessage reactionMessage = objectMapper.readValue(message, SetCatReactionMessage.class);
            Long userId = reactionMessage.getUserId();
            Long catId = reactionMessage.getCatId();
            String action = reactionMessage.getAction();

            LOGGER.info("Processing SetCatReaction - userId: {}, catId: {}, action: {}", userId, catId, action);

            CatReaction.ReactionType reactionType = CatReaction.ReactionType.valueOf(action);
            dbService.setReaction(userId, catId, reactionType);

            LOGGER.info("Successfully set reaction for user {} on cat {}: {}", userId, catId, action);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid action type in message: {}", message, e);
        } catch (Exception e) {
            LOGGER.error("Failed to process set cat reaction: {}", message, e);
        }
    }
}
