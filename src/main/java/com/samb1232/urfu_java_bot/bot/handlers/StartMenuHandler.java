package com.samb1232.urfu_java_bot.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.samb1232.urfu_java_bot.bot.TelegramService;
import com.samb1232.urfu_java_bot.bot.keyboards.InlineKeyboardFactory;

public class StartMenuHandler {
    private final TelegramService telegramService;

    public StartMenuHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }


    public void sendStartMenu(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Добро пожаловать! Отправьте мне изображение или просмотрите сохраненные.");
        message.setReplyMarkup(InlineKeyboardFactory.createStartMenuKeyboard());
        telegramService.execute(message);
    }

    public void returnToStartMenu(Long chatId, Integer messageId) throws TelegramApiException {
        telegramService.deleteMessage(chatId, messageId);
        sendStartMenu(chatId);
    }
}