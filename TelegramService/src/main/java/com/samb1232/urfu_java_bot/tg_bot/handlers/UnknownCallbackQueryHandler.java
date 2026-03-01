package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.springframework.statemachine.StateMachine;

import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

public abstract class UnknownCallbackQueryHandler {

    private final TelegramApiService telegramApiService;

    public UnknownCallbackQueryHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    protected void returnToMainMenu(Long chatId, StateMachine<BotState, BotEvent> stateMachine) {
        stateMachine.sendEvent(BotEvent.START);
        telegramApiService.sendMessageWithKeyboard(chatId, TextFields.MAIN_MENU_TEXT,
                KeyboardFactory.createMainMenuKeyboard());
    }

    protected void processUnkownCallbackQuery(UserCallback callbackQuery,
            StateMachine<BotState, BotEvent> stateMachine) {
        returnToMainMenu(callbackQuery.getChatId(), stateMachine);
    }

    protected void processUnknownMessage(UserMessage message, StateMachine<BotState, BotEvent> stateMachine) {
        returnToMainMenu(message.getChatId(), stateMachine);
    }
}
