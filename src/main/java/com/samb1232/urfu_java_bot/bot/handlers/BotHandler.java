package com.samb1232.urfu_java_bot.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotHandler {
    void handle(Update update) throws TelegramApiException;
}