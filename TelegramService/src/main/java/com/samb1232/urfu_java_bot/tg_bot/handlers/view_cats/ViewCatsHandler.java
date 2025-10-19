package com.samb1232.urfu_java_bot.tg_bot.handlers.view_cats;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

@Service
public class ViewCatsHandler extends UnknownCallbackQueryHandler implements UpdateHandler {

    private final TelegramApiService telegramApiService;

    public ViewCatsHandler(TelegramApiService telegramApiService) {
        super(telegramApiService);
        this.telegramApiService = telegramApiService;
    }

    public void onStart(Long chatId) {
        telegramApiService.sendMessage(chatId, "Вы выбрали: Смотреть котиков");
    }
    
    @Override
    public void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine) {
        if (updateInfo.hasUserCallback()) {
            processCallbackQuery(updateInfo.getUserCallback(), stateMachine);
        }
    }

    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();

        switch (callbackData) {
            default -> processUnkownCallbackQuery(callbackQuery, stateMachine);
        }

        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

}
