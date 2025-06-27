package com.samb1232.urfu_java_bot.tg_bot.handlers;

import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.database.DBService;
import com.samb1232.urfu_java_bot.database.entities.User;
import com.samb1232.urfu_java_bot.dto.TGUser;
import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;


public class CommandHandler implements UpdateHandler {
    private final TelegramApiService telegramApiService;
    private final DBService dbService;

    public CommandHandler(TelegramApiService telegramApiService, DBService dbService) {
        this.telegramApiService = telegramApiService;
        this.dbService = dbService;
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
        TGUser tgUser = message.getTGUser();
        User user = dbService.getOrCreateUser(tgUser);
        
        telegramApiService.sendMessage(chatId, String.format(TextFields.HELLO_MESSAGE_TEXT, user.getName()));
        telegramApiService.sendMessage(chatId, TextFields.MAIN_MENU_TEXT);
    }

    private void processUnknownCommand(UserMessage message) {
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, TextFields.UNKNOWN_COMMAND_TEXT);
    }
}