package com.samb1232.urfu_java_bot.tg_bot.handlers.main_menu;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.samb1232.urfu_java_bot.constants.MenuCallbackData;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.add_cat.AddCatHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.my_cats.MyCatsHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.view_cats.ViewCatsHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;

@Service
public class MainMenuHandler extends UnknownCallbackQueryHandler implements UpdateHandler {

    private final TelegramApiService telegramApiService;
    private final AddCatHandler addCatHandler;
    private final MyCatsHandler myCatsHandler;
    private final ViewCatsHandler viewCatsHandler;

    public MainMenuHandler(
        TelegramApiService telegramApiService, 
        AddCatHandler addCatHandler,
        MyCatsHandler myCatsHandler,
        ViewCatsHandler viewCatsHandler
    ) {
        super(telegramApiService);
        this.telegramApiService = telegramApiService;
        this.addCatHandler = addCatHandler;
        this.myCatsHandler = myCatsHandler;
        this.viewCatsHandler = viewCatsHandler;
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
                stateMachine.sendEvent(BotEvent.MY_CATS_COMMAND);
                myCatsHandler.onStart(chatId);
            }

            case MenuCallbackData.VIEW_CATS_CALLBACK -> {
                stateMachine.sendEvent(BotEvent.VIEW_CATS_COMMAND);
                viewCatsHandler.onStart(chatId);
            }

            case MenuCallbackData.ADD_CAT_CALLBACK -> {
                stateMachine.sendEvent(BotEvent.ADD_CAT_COMMAND);
                addCatHandler.onStart(chatId);
            }

            default -> processUnkownCallbackQuery(callbackQuery, stateMachine);
        }

        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }
}
