package com.samb1232.urfu_java_bot.tg_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.samb1232.urfu_java_bot.dto.UserMessage;


public class TelegramApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramApiService.class);
    private final TelegramLongPollingBot bot;

    public TelegramApiService(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending message", e);
        }
    }
    
    public static UserMessage toUserMessage(Message message) {
        return new UserMessage(
            message.getChatId(),
            message.getText()
        );
    }
}