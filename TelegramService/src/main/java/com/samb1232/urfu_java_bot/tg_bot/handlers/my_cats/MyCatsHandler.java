package com.samb1232.urfu_java_bot.tg_bot.handlers.my_cats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.samb1232.common.dto.CatInfo;
import com.samb1232.common.dto.DeleteCatMessage;
import com.samb1232.common.dto.GetMyCatsMessage;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.messaging.RabbitMqService;
import com.samb1232.urfu_java_bot.services.CatCacheService;
import com.samb1232.urfu_java_bot.services.FileStorageService;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

@Service
public class MyCatsHandler extends UnknownCallbackQueryHandler implements UpdateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCatsHandler.class);

    private final TelegramApiService telegramApiService;
    private final RabbitMqService rabbitMqService;
    private final CatCacheService catCacheService;
    private final FileStorageService fileStorageService;

    public MyCatsHandler(TelegramApiService telegramApiService, RabbitMqService rabbitMqService, CatCacheService catCacheService, FileStorageService fileStorageService) {
        super(telegramApiService);
        this.telegramApiService = telegramApiService;
        this.rabbitMqService = rabbitMqService;
        this.catCacheService = catCacheService;
        this.fileStorageService = fileStorageService;
    }

    public void onStart(Long chatId) {
        telegramApiService.sendMessage(chatId, "Вы выбрали: Мои котики");
        sendGetMyCatsRequest(chatId);
    }

    @Override
    public void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine) {
        // Send request to get user's cats when entering MY_CATS state
        Long userId = null;
        if (updateInfo.hasUserCallback()) {
            userId = updateInfo.getUserCallback().getChatId();
            processCallbackQuery(updateInfo.getUserCallback(), stateMachine);
        } else if (updateInfo.hasUserMessage() && updateInfo.getUserMessage().getTGUser() != null) {
            userId = updateInfo.getUserMessage().getTGUser().getId();
        }
    }

    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();
        Long chatId = callbackQuery.getChatId();

        // Handle cat detail view
        if (callbackData.startsWith("view_cat_")) {
            try {
                Long catId = Long.valueOf(callbackData.substring("view_cat_".length()));
                showCatDetails(chatId, catId);
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid cat ID in callback data: {}", callbackData, e);
                telegramApiService.sendMessage(chatId, "Ошибка при отображении котика");
            }
        } else if (callbackData.startsWith("delete_cat_")) {
            try {
                Long catId = Long.valueOf(callbackData.substring("delete_cat_".length()));
                deleteCat(chatId, catId);
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid cat ID in delete callback data: {}", callbackData, e);
                telegramApiService.sendMessage(chatId, "Ошибка при удалении котика");
            }
        } else {
            processUnkownCallbackQuery(callbackQuery, stateMachine);
        }

        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

    private void showCatDetails(Long chatId, Long catId) {
        CatInfo cat = catCacheService.getCatById(catId);

        if (cat == null) {
            telegramApiService.sendMessage(chatId, "Котик не найден. Попробуйте обновить список.");
            return;
        }

        String caption = String.format(
            "🐱 %s\n\n" +
            "👍: %d\n" +
            "👎: %d",
            cat.getName(),
            cat.getLikes(),
            cat.getDislikes()
        );

        try {
            byte[] photoBytes = fileStorageService.readPhoto(cat.getPhotoPath());
            telegramApiService.sendPhoto(chatId, photoBytes, caption, KeyboardFactory.createDeleteCatKeyboard(catId));
        } catch (Exception e) {
            LOGGER.error("Failed to load photo from file: {}", cat.getPhotoPath(), e);
            telegramApiService.sendMessage(chatId, "Ошибка при загрузке фото котика.");
        }
    }

    private void sendGetMyCatsRequest(Long userId) {
        GetMyCatsMessage getMyCatsMessage = new GetMyCatsMessage(userId);
        String payload = String.format("{\"userId\":%d}", getMyCatsMessage.getUserId());
        rabbitMqService.sendToGetMyCatsQueue(payload);
        LOGGER.info("Sent get my cats request for user: {}", userId);
    }

    private void deleteCat(Long chatId, Long catId) {
        DeleteCatMessage deleteCatMessage = new DeleteCatMessage(catId);
        String payload = String.format("{\"catId\":%d}", deleteCatMessage.getCatId());
        rabbitMqService.sendToDeleteCatRequestQueue(payload);
        telegramApiService.sendMessage(chatId, "Котик удален");
        LOGGER.info("Sent delete cat request for cat ID: {}", catId);
    }
}
