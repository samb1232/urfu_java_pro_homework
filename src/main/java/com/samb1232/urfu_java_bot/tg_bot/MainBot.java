package com.samb1232.urfu_java_bot.tg_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.samb1232.urfu_java_bot.database.DBService;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;
import com.samb1232.urfu_java_bot.tg_bot.handlers.CommandHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.main_menu.MainMenuHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.StateMachineService;
import com.samb1232.urfu_java_bot.utils.TelegramUpdateUtils;

@Component
public class MainBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainBot.class);
    private final TelegramApiService telegramApiService;
    private final StateMachineService stateMachineService;
    private final CommandHandler commandHandler;
    private final MainMenuHandler mainMenuHandler;

    public MainBot(
        @Value("${bot.token}") String botToken,
        DBService dbService,
        StateMachineService stateMachineService,
        KeyboardFactory keyboardFactory
    ) {
        super(botToken);
        this.telegramApiService = new TelegramApiService(this);
        this.stateMachineService = stateMachineService;
        this.commandHandler = new CommandHandler(telegramApiService, dbService, keyboardFactory);
        this.mainMenuHandler = new MainMenuHandler(telegramApiService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOGGER.debug("Update recieved");
        
        UpdateInfo updateInfo = TelegramApiService.toUpdateInfo(update);

        Long chatId = TelegramUpdateUtils.getChatIdFromUpdateInfo(updateInfo);

        StateMachine<BotState, BotEvent> stateMachine = stateMachineService.getStateMachine(chatId);

        if (TelegramUpdateUtils.hasCommand(updateInfo)) {
            commandHandler.handle(updateInfo, stateMachine);
        }
        else {
            BotState currentState = stateMachine.getState().getId();
            switch (currentState) {
                case BotState.MAIN_MENU -> {
                    mainMenuHandler.handle(updateInfo, stateMachine);
                }
                case BotState.ADD_CAT -> {
                }
                case BotState.VIEW_CATS -> {
                }
                case BotState.MY_CATS -> {
                }
                default -> throw new AssertionError();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "urfu_java_dz_bot";
    }
}