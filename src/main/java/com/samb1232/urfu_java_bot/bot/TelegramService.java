package com.samb1232.urfu_java_bot.bot;

import java.io.File;
import java.io.IOException;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramService {
    private final KittyBot bot;

    public TelegramService(KittyBot bot) {
        this.bot = bot;
    }

    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build());
    }

    public void deleteMessage(Long chatId, Integer messageId) throws TelegramApiException {
        bot.execute(DeleteMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build());
    }

    public void answerCallbackQuery(String callbackId) throws TelegramApiException {
        bot.execute(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .build());
    }

    public File downloadFile(String fileId) throws TelegramApiException, IOException {
        org.telegram.telegrambots.meta.api.objects.File file = bot.execute(
            org.telegram.telegrambots.meta.api.methods.GetFile.builder()
                .fileId(fileId)
                .build());
        return bot.downloadFile(file);
    }

    public Message execute(SendPhoto photo) throws TelegramApiException {
        return bot.execute(photo);
    }

    public Message execute(SendMessage message) throws TelegramApiException {
        return bot.execute(message);
    }
}