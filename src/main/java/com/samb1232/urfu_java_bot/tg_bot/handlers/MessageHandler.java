package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;

import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;

public class MessageHandler implements UpdateHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private final TelegramApiService telegramApiService;

    public MessageHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    @Override
    public void handle(Message message) {
        LOGGER.debug("Handling message");
        if (!message.hasText()) {
            return;
        }
        
        String messageText = message.getText();
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, messageText);
    }
}