package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;

public class MessageHandler implements UpdateHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private final TelegramApiService telegramApiService;

    public MessageHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    @Override
    public void handle(UserMessage message) {
        LOGGER.debug("Handling message");
        String messageText = message.getText();
        if (messageText.isEmpty()) {
            return;
        }
        
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, messageText);
    }
}