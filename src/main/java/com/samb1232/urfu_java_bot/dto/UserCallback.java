package com.samb1232.urfu_java_bot.dto;

public class UserCallback {
    private final String callbackData;
    private final String callbackId;
    private final Long chatId;

    public UserCallback(String callbackData, String callbackId, java.lang.Long chatId) {
        this.callbackData = callbackData;
        this.callbackId = callbackId;
        this.chatId = chatId;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public Long getChatId() {
        return chatId;
    }
}
