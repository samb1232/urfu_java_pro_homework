package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Message;

import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;

public class CommandHandler {
    private final TelegramApiService telegramApiService;

    public CommandHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    public void handleCommand(Message message) {
        String command = message.getText();

        switch (command) {
            case "/start" -> processStartCommand(message);
            default -> processUnknownCommand(message);
        }
    }

    private void processStartCommand(Message message) {
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, "Привет!");
    }

    private void processUnknownCommand(Message message) {
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, "Неизвестная команда");
    }
}