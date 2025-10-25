package com.samb1232.urfu_java_bot.tg_bot.handlers.view_cats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.samb1232.common.dto.ViewRandomCatRequestMessage;
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
        }
    }

    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();

        switch (callbackData) {
            default -> processUnkownCallbackQuery(callbackQuery, stateMachine);
        }

        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

}
