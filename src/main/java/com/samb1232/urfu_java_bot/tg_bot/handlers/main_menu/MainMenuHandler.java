package com.samb1232.urfu_java_bot.tg_bot.handlers.main_menu;

import org.springframework.statemachine.StateMachine;

import com.samb1232.urfu_java_bot.constants.CallbackData;
import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

public class MainMenuHandler implements UpdateHandler {

    private final TelegramApiService telegramApiService;

    public MainMenuHandler(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    @Override
    public void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine) {
        if (updateInfo.hasUserCallback()) {
            processCallbackQuery(updateInfo.getUserCallback(), stateMachine);
        }
    }
    
    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();
        Long chatId = callbackQuery.getChatId();
        
        switch (callbackData) {
            case CallbackData.MY_CATS_CALLBACK -> {
                telegramApiService.sendMessage(chatId, "Вы выбрали: Мои котики");
                stateMachine.sendEvent(BotEvent.MY_CATS_COMMAND);
            }
                
            case CallbackData.VIEW_CATS_CALLBACK -> {
                telegramApiService.sendMessage(chatId, "Вы выбрали: Смотреть котиков");
                stateMachine.sendEvent(BotEvent.VIEW_CATS_COMMAND);
            }
                
            case CallbackData.ADD_CAT_CALLBACK -> {
                telegramApiService.sendMessage(chatId, "Вы выбрали: Добавить котика");
                stateMachine.sendEvent(BotEvent.ADD_CAT_COMMAND);
            }
                
            default -> telegramApiService.sendMessage(chatId, TextFields.UNKNOWN_CALLBACK_TEXT);
        }
        
        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }
}