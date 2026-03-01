package com.samb1232.urfu_java_bot.tg_bot.handlers.view_cats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.samb1232.common.dto.SetCatReactionMessage;
import com.samb1232.common.dto.ViewRandomCatRequestMessage;
import com.samb1232.urfu_java_bot.constants.MenuCallbackData;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.messaging.RabbitMqService;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

@Service
public class ViewCatsHandler extends UnknownCallbackQueryHandler implements UpdateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewCatsHandler.class);

    private final TelegramApiService telegramApiService;
    private final RabbitMqService rabbitMqService;

    public ViewCatsHandler(TelegramApiService telegramApiService, RabbitMqService rabbitMqService) {
        super(telegramApiService);
        this.telegramApiService = telegramApiService;
        this.rabbitMqService = rabbitMqService;
    }

    public void onStart(Long chatId) {
        telegramApiService.sendMessage(chatId, "Вы выбрали: Смотреть котиков");
        sendViewRandomCatRequest(chatId);
    }

    private void sendViewRandomCatRequest(Long userId) {
        ViewRandomCatRequestMessage viewRandomCatRequestMessage = new ViewRandomCatRequestMessage(userId);
        String payload = String.format("{\"userId\":%d}", viewRandomCatRequestMessage.getUserId());
        rabbitMqService.sendToViewRandomCatQueue(payload);
        LOGGER.info("Sent view random cat request for user: {}", userId);
    }

    @Override
    public void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine) {
        if (updateInfo.hasUserCallback()) {
            processCallbackQuery(updateInfo.getUserCallback(), stateMachine);
        } else if (updateInfo.hasUserMessage()) {
            processUnknownMessage(updateInfo.getUserMessage(), stateMachine);
        }
    }

    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();
        Long chatId = callbackQuery.getChatId();

        if (callbackData.startsWith(MenuCallbackData.LIKE_CAT_PREFIX)) {
            handleLikeButton(callbackData, chatId);
        } else if (callbackData.startsWith(MenuCallbackData.DISLIKE_CAT_PREFIX)) {
            handleDislikeButton(callbackData, chatId);
        } else if (callbackData.equals(MenuCallbackData.NEXT_RANDOM_CAT)) {
            handleNextButton(chatId);
        } else {
            processUnkownCallbackQuery(callbackQuery, stateMachine);
        }

        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

    private void handleLikeButton(String callbackData, Long chatId) {
        try {
            Long catId = Long.valueOf(callbackData.substring(MenuCallbackData.LIKE_CAT_PREFIX.length()));
            LOGGER.info("User {} liked cat {}", chatId, catId);

            sendCatReaction(chatId, catId, "LIKE");
            telegramApiService.sendMessage(chatId, "❤️ Вам понравился котик!");
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid cat ID in like callback: {}", callbackData, e);
        }
    }

    private void handleDislikeButton(String callbackData, Long chatId) {
        try {
            Long catId = Long.valueOf(callbackData.substring(MenuCallbackData.DISLIKE_CAT_PREFIX.length()));
            LOGGER.info("User {} disliked cat {}", chatId, catId);

            sendCatReaction(chatId, catId, "DISLIKE");
            telegramApiService.sendMessage(chatId, "👎 Жаль, что котик не понравился");
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid cat ID in dislike callback: {}", callbackData, e);
        }
    }

    private void sendCatReaction(Long userId, Long catId, String action) {
        SetCatReactionMessage reactionMessage = new SetCatReactionMessage(userId, catId, action);
        String payload = String.format("{\"userId\":%d,\"catId\":%d,\"action\":\"%s\"}",
                reactionMessage.getUserId(),
                reactionMessage.getCatId(),
                reactionMessage.getAction());
        rabbitMqService.sendToSetCatReactionQueue(payload);
        LOGGER.info("Sent cat reaction: user={}, cat={}, action={}", userId, catId, action);
    }

    private void handleNextButton(Long chatId) {
        LOGGER.info("User {} requested next random cat", chatId);
        sendViewRandomCatRequest(chatId);
    }

}
