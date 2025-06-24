package com.samb1232.urfu_java_bot.tg_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.samb1232.urfu_java_bot.tg_bot.handlers.CommandHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.MessageHandler;

@Component
public class MainBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainBot.class);
    private final TelegramApiService telegramApiService;
    private final MessageHandler messageHandler;
    private final CommandHandler commandHandler;

    public MainBot(@Value("${bot.token}") String botToken) {
        super(botToken);
        this.telegramApiService = new TelegramApiService(this);
        this.messageHandler = new MessageHandler(telegramApiService);
        this.commandHandler = new CommandHandler(telegramApiService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOGGER.debug("Update recieved");
        
        Message message = update.getMessage();
        if (message.isCommand()) {
            commandHandler.handleCommand(message);
        }
        else {
            messageHandler.handleMessage(message);
        }
        
        
    }

    @Override
    public String getBotUsername() {
        return "urfu_java_dz_bot";
    }
}