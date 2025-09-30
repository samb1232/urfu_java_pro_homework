package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.springframework.statemachine.StateMachine;

import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

public abstract class UnknownCallbackQueryHandler {

    private final TelegramApiService telegramApiService;

    public UnknownCallbackQueryHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }
    
    protected void processUnkownCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        Long chatId = callbackQuery.getChatId();
        telegramApiService.sendMessage(chatId, TextFields.UNKNOWN_CALLBACK_TEXT);
        stateMachine.sendEvent(BotEvent.START);

        telegramApiService.sendMessageWithKeyboard(chatId, TextFields.MAIN_MENU_TEXT, KeyboardFactory.createMainMenuKeyboard());
    }
}
