package com.samb1232.urfu_java_bot.dto;


public class UserMessage {
    private final long chatId;
    private final String text;
    
    public UserMessage(long chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }
    
    public long getChatId() {
        return chatId;
    }
    
    public String getText() {
        return text;
    }
    
}