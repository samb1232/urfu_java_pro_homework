package com.samb1232.urfu_java_bot.tg_bot.handlers.main_menu;

import org.springframework.statemachine.StateMachine;

import com.samb1232.urfu_java_bot.constants.MenuCallbackData;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

public class MainMenuHandler extends UnknownCallbackQueryHandler implements UpdateHandler {

    private final TelegramApiService telegramApiService;

    public MainMenuHandler(TelegramApiService telegramApiService) {
        super(telegramApiService);
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
            case MenuCallbackData.MY_CATS_CALLBACK -> {
                telegramApiService.sendMessage(chatId, "Вы выбрали: Мои котики");
                stateMachine.sendEvent(BotEvent.MY_CATS_COMMAND);
            }
                
            case MenuCallbackData.VIEW_CATS_CALLBACK -> {
                telegramApiService.sendMessage(chatId, "Вы выбрали: Смотреть котиков");
                stateMachine.sendEvent(BotEvent.VIEW_CATS_COMMAND);
            }
                
            case MenuCallbackData.ADD_CAT_CALLBACK -> {
                telegramApiService.sendMessage(chatId, "Вы выбрали: Добавить котика");
                stateMachine.sendEvent(BotEvent.ADD_CAT_COMMAND);
            }
                
            default -> processUnkownCallbackQuery(callbackQuery, stateMachine);
        }
        
        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

    
}
