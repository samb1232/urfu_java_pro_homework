package com.samb1232.urfu_java_bot.tg_bot.handlers;

import com.samb1232.urfu_java_bot.dto.UserMessage;


public interface UpdateHandler {
    public abstract void handle(UserMessage message);
}
