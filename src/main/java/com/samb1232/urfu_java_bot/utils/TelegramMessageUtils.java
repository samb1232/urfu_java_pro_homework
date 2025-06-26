package com.samb1232.urfu_java_bot.utils;

import com.samb1232.urfu_java_bot.dto.UserMessage;

public class TelegramMessageUtils {
    public static boolean isCommand(UserMessage message) {
        return message.getText() != null && message.getText().startsWith("/");
    }
}