package com.samb1232.urfu_java_bot.tg_bot.handlers;

import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;

public class CommandHandler implements UpdateHandler {
    private final TelegramApiService telegramApiService;

    public CommandHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    @Override
    public void handle(UserMessage message) {
        String command = message.getText();

        switch (command) {
            case "/start" -> processStartCommand(message);
            default -> processUnknownCommand(message);
        }
    }

    private void processStartCommand(UserMessage message) {
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, TextFields.HELLO_MESSAGE_TEXT);
    }

    private void processUnknownCommand(UserMessage message) {
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, TextFields.UNKNOWN_COMMAND_TEXT);
    }
}