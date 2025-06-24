package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Message;

import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;

public class MessageHandler {
    private final TelegramApiService telegramApiService;

    public MessageHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    public void handleTextMessage(Message message) {
        if (!message.hasText()) {
            return;
        }
        
        String messageText = message.getText();
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, messageText);
    }
}