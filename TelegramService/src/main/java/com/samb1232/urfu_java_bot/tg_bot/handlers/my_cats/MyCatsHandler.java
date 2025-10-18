package com.samb1232.urfu_java_bot.tg_bot.handlers.my_cats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.samb1232.urfu_java_bot.dto.GetMyCatsMessage;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.messaging.RabbitMqService;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

@Service
public class MyCatsHandler extends UnknownCallbackQueryHandler implements UpdateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCatsHandler.class);

    private final TelegramApiService telegramApiService;
    private final RabbitMqService rabbitMqService;

    public MyCatsHandler(TelegramApiService telegramApiService, RabbitMqService rabbitMqService) {
        super(telegramApiService);
        this.telegramApiService = telegramApiService;
        this.rabbitMqService = rabbitMqService;
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

        if (userId != null) {
            sendGetMyCatsRequest(userId);
        }
    }

    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();

        switch (callbackData) {
            default -> processUnkownCallbackQuery(callbackQuery, stateMachine);
        }

        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

    private void sendGetMyCatsRequest(Long userId) {
        GetMyCatsMessage getMyCatsMessage = new GetMyCatsMessage(userId);
        String payload = String.format("{\"userId\":%d}", getMyCatsMessage.getUserId());
        rabbitMqService.sendToGetMyCatsQueue(payload);
        LOGGER.info("Sent get my cats request for user: {}", userId);
    }

}
