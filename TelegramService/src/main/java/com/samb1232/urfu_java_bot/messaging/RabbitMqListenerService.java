package com.samb1232.urfu_java_bot.messaging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.samb1232.common.dto.CatInfo;
import com.samb1232.common.dto.MyCatsResponse;
import com.samb1232.common.dto.ViewRandomCatResponseMessage;
import com.samb1232.urfu_java_bot.services.CatCacheService;
import com.samb1232.urfu_java_bot.services.FileStorageService;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;

@Service
public class RabbitMqListenerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqListenerService.class);

    private final CatCacheService catCacheService;
    private final TelegramApiService telegramApiService;
    private final FileStorageService fileStorageService;

    public RabbitMqListenerService(CatCacheService catCacheService, TelegramApiService telegramApiService, FileStorageService fileStorageService) {
        this.catCacheService = catCacheService;
        this.telegramApiService = telegramApiService;
        this.fileStorageService = fileStorageService;
    }

    @RabbitListener(queues = "${app.rabbitmq.my_cats_response_queue}")
    public void handleMyCatsResponse(MyCatsResponse myCatsResponse) {
        LOGGER.info("Received message from my cats response queue: {}", myCatsResponse);

        try {
            Long userId = myCatsResponse.getUserId();
            List<CatInfo> cats = myCatsResponse.getCats();

            LOGGER.info("Processing MyCatsResponse - userId: {}, catsCount: {}", userId, cats.size());

            catCacheService.storeCatsForUser(userId, cats);

            if (cats.isEmpty()) {
                telegramApiService.sendMessage(userId, "У вас пока нет котиков 😿");
            } else {
                var keyboard = KeyboardFactory.createCatsKeyboard(cats);
                telegramApiService.sendMessageWithKeyboard(userId,
                    String.format("Ваши котики (%d):", cats.size()),
                    keyboard);
            }

            LOGGER.info("Successfully processed my cats response for user: {}", userId);

        } catch (Exception e) {
            LOGGER.error("Failed to process my cats response", e);
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.view_random_cat_response_queue}")
    public void handleViewRandomCatResponse(ViewRandomCatResponseMessage response) {
        LOGGER.info("Received message from view random cat response queue: {}", response);

        try {
            Long userId = response.getUserId();
            Long catId = response.getCatId();
            String name = response.getName();
            String photoPath = response.getPhotoPath();
            int likesCount = response.getLikesCount();
            int dislikesCount = response.getDislikesCount();

            LOGGER.info("Processing ViewRandomCatResponse - userId: {}, catId: {}, name: {}", userId, catId, name);

            String caption = String.format("🐱 %s", name);
            var keyboard = KeyboardFactory.createViewRandomCatKeyboard(catId, likesCount, dislikesCount);

            try {
                byte[] photoBytes = fileStorageService.readPhoto(photoPath);
                telegramApiService.sendPhoto(userId, photoBytes, caption, keyboard);
                LOGGER.info("Successfully sent random cat (ID: {}) to user: {}", catId, userId);
            } catch (Exception e) {
                LOGGER.error("Failed to load photo from file: {}", photoPath, e);
                telegramApiService.sendMessage(userId, "Ошибка при загрузке фото котика.");
            }

        } catch (Exception e) {
            LOGGER.error("Failed to process view random cat response", e);
        }
    }

}
