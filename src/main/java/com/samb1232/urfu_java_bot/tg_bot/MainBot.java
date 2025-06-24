package com.samb1232.urfu_java_bot.tg_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MainBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainBot.class);
    private final TelegramApiService telegramApiService;

    public MainBot(@Value("${bot.token}") String botToken) {
        super(botToken);
        this.telegramApiService = new TelegramApiService(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Message msg = update.getMessage();
        sendEchoMessage(msg);
    }

    private void sendEchoMessage(Message msg) {
        var messageText = msg.getText();
        var chatId = msg.getChatId();
        telegramApiService.sendMessage(chatId, messageText);
    }

    @Override
    public String getBotUsername() {
        return "urfu_java_dz_bot";
    }
}