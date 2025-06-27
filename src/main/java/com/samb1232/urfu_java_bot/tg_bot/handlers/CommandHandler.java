package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.springframework.statemachine.StateMachine;

import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.database.DBService;
import com.samb1232.urfu_java_bot.database.entities.User;
import com.samb1232.urfu_java_bot.dto.TGUser;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;


public class CommandHandler implements UpdateHandler {
    private final TelegramApiService telegramApiService;
    private final DBService dbService;
    private final KeyboardFactory keyboardFactory;

    public CommandHandler(
        TelegramApiService telegramApiService, 
        DBService dbService,
        KeyboardFactory keyboardFactory) {
        this.telegramApiService = telegramApiService;
        this.dbService = dbService;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine) {
        UserMessage message = updateInfo.getUserMessage();
        String command = message.getText();

        switch (command) {
            case "/start" -> processStartCommand(message, stateMachine);
            default -> processUnknownCommand(message);
        }
    }

    private void processStartCommand(UserMessage message, StateMachine<BotState, BotEvent> stateMachine) {
        stateMachine.sendEvent(BotEvent.START);
        Long chatId = message.getChatId();
        TGUser tgUser = message.getTGUser();
        User user = dbService.getOrCreateUser(tgUser);
        
        telegramApiService.sendMessage(chatId, String.format(TextFields.HELLO_MESSAGE_TEXT, user.getName()));

        telegramApiService.sendMessageWithKeyboard(chatId, TextFields.MAIN_MENU_TEXT, keyboardFactory.createMainMenuKeyboard());
    }

    private void processUnknownCommand(UserMessage message) {
        Long chatId = message.getChatId();
        telegramApiService.sendMessage(chatId, TextFields.UNKNOWN_COMMAND_TEXT);
    }
}