package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface UpdateHandler {
    public abstract void handle(Message message);
}
