package com.samb1232.urfu_java_bot.dto;

public class UpdateInfo {
    private final UserMessage userMessage;
    private final UserCallback userCallback;

    public UpdateInfo(UserMessage userMessage, UserCallback userCallback) {
        this.userMessage = userMessage;
        this.userCallback = userCallback;
    }

    public UserMessage getUserMessage() {
        return userMessage;
    }

    public UserCallback getUserCallback() {
        return userCallback;
    }
    
    public boolean hasUserCallback() {
        return userCallback != null;
    }

    public boolean hasUserMessage() {
        return userMessage != null;
    }
    
}
