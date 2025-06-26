package com.samb1232.urfu_java_bot.tg_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.samb1232.urfu_java_bot.database.DBService;
import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.tg_bot.handlers.CommandHandler;
import com.samb1232.urfu_java_bot.utils.TelegramMessageUtils;

@Component
public class MainBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainBot.class);
    private final TelegramApiService telegramApiService;
    private final CommandHandler commandHandler;

    public MainBot(@Value("${bot.token}") String botToken, DBService dbService) {
        super(botToken);
        this.telegramApiService = new TelegramApiService(this);
        this.commandHandler = new CommandHandler(telegramApiService, dbService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOGGER.debug("Update recieved");
        
        UserMessage message = TelegramApiService.toUserMessage(update.getMessage());
        if (TelegramMessageUtils.isCommand(message)) {
            commandHandler.handle(message);
        }
        
        
    }

    @Override
    public String getBotUsername() {
        return "urfu_java_dz_bot";
    }
}