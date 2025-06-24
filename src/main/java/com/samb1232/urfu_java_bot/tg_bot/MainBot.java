package com.samb1232.urfu_java_bot.tg_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MainBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainBot.class);

    public MainBot(@Value("${bot.token}")String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }
        Message msg = update.getMessage();
        sendEchoMessage(msg);

    }

    private void sendEchoMessage(Message msg) {
        var messageText = msg.getText();
        var chatId = msg.getChatId();
        sendMessage(chatId, messageText);
    }
    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending message", e);
        }
    }
    @Override
    public String getBotUsername() {
        return "urfu_java_dz_bot";
    }
}
