package com.samb1232.urfu_java_bot.messaging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.samb1232.common.dto.CatInfo;
import com.samb1232.common.dto.MyCatsResponse;
import com.samb1232.urfu_java_bot.services.CatCacheService;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;

@Service
public class RabbitMqListenerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqListenerService.class);

    private final CatCacheService catCacheService;
    private final TelegramApiService telegramApiService;

    public RabbitMqListenerService(CatCacheService catCacheService, TelegramApiService telegramApiService) {
        this.catCacheService = catCacheService;
        this.telegramApiService = telegramApiService;
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

    
}
