package com.samb1232.urfu_java_bot.utils;

import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserMessage;

public class TelegramUpdateUtils {
    public static boolean hasCommand(UpdateInfo updateInfo) {
        if (!updateInfo.hasUserMessage()) return false;
        UserMessage message = updateInfo.getUserMessage();
        return message.getText() != null && message.getText().startsWith("/");
    }

    public static Long getChatIdFromUpdateInfo(UpdateInfo updateInfo) {
        Long chatId = null;
        if (updateInfo.hasUserCallback()) {
            chatId = updateInfo.getUserCallback().getChatId();
        }
        else if (updateInfo.hasUserMessage()) {
            chatId = updateInfo.getUserMessage().getChatId();
        }
        return chatId;
    }
}