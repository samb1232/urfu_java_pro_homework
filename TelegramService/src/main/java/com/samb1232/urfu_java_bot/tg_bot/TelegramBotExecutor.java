package com.samb1232.urfu_java_bot.tg_bot;

import java.io.Serializable;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramBotExecutor {

    <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException;

    Message execute(SendPhoto sendPhoto) throws TelegramApiException;

    String getBotToken();
}
