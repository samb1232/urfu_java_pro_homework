package com.samb1232.urfu_java_bot.tg_bot.handlers.add_cat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.samb1232.urfu_java_bot.constants.MenuCallbackData;
import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.dto.AddCatMessage;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.messaging.RabbitMqService;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

@Service
public class AddCatHandler extends UnknownCallbackQueryHandler implements UpdateHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddCatHandler.class);
    private final TelegramApiService telegramApiService;
    private final RabbitMqService rabbitMqService;

    private final Map<Long, Boolean> waitingForNameByChatId = new ConcurrentHashMap<>();
    private final Map<Long, String> photoBase64ByChatId = new ConcurrentHashMap<>();

    public AddCatHandler(TelegramApiService telegramApiService, RabbitMqService rabbitMqService) {
        super(telegramApiService);
        this.telegramApiService = telegramApiService;
        this.rabbitMqService = rabbitMqService;
    }

    @Override
    public void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine) {
        if (updateInfo.hasUserCallback()) {
            processCallbackQuery(updateInfo.getUserCallback(), stateMachine);
            return;
        }
        if (updateInfo.hasUserMessage()) {
            processMessage(updateInfo.getUserMessage());
        }
    }

    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();
        Long chatId = callbackQuery.getChatId();

        switch (callbackData) {
            case MenuCallbackData.BACK_TO_MAIN_MENU_CALLBACK -> {
                waitingForNameByChatId.remove(chatId);
                photoBase64ByChatId.remove(chatId);
                stateMachine.sendEvent(BotEvent.START);
                telegramApiService.sendMessageWithKeyboard(chatId, TextFields.MAIN_MENU_TEXT,
                        KeyboardFactory.createMainMenuKeyboard());
            }
            default -> processUnkownCallbackQuery(callbackQuery, stateMachine);
        }

        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

    private void processMessage(UserMessage userMessage) {
        Long chatId = userMessage.getChatId();
        boolean waitingForName = waitingForNameByChatId.getOrDefault(chatId, Boolean.FALSE);

        if (!waitingForName) {
            if (userMessage.getPhotoFileId() != null) {
                String photoBase64 = telegramApiService.downloadFileAsBase64(userMessage.getPhotoFileId());
                if (photoBase64 != null) {
                    photoBase64ByChatId.put(chatId, photoBase64);
                }
            }
            telegramApiService.sendMessageWithKeyboard(chatId, TextFields.ADD_CAT_PHOTO_RECEIVED_TEXT,
                    KeyboardFactory.createBackToMainMenuKeyboard());
            waitingForNameByChatId.put(chatId, Boolean.TRUE);
            return;
        }

        String name = userMessage.getText();
        if (name != null && !name.startsWith("/")) {
            LOGGER.info("User added a cat");
            Long userId = userMessage.getTGUser() != null ? userMessage.getTGUser().getId() : chatId;
            String photoBase64 = photoBase64ByChatId.get(chatId);
            AddCatMessage addCatMessage = new AddCatMessage(userId, photoBase64, name);
            try {
                String payload = String.format("{\"userId\":%d,\"photoBase64\":\"%s\",\"catName\":\"%s\"}",
                        addCatMessage.getUserId(),
                        photoBase64 == null ? "" : photoBase64,
                        name.replace("\"", "\\\""));
                rabbitMqService.sendToQueue(payload);
            } catch (Exception e) {
                LOGGER.error("Failed to enqueue AddCatMessage", e);
            }
            telegramApiService.sendMessageWithKeyboard(chatId, TextFields.ADD_CAT_DONE_TEXT,
                    KeyboardFactory.createBackToMainMenuKeyboard());
            waitingForNameByChatId.remove(chatId);
            photoBase64ByChatId.remove(chatId);
            return;
        }

        // If invalid input, re-ask
        telegramApiService.sendMessageWithKeyboard(chatId, TextFields.ADD_CAT_PHOTO_RECEIVED_TEXT,
                KeyboardFactory.createBackToMainMenuKeyboard());
    }

}
