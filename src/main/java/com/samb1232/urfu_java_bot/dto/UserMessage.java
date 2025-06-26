package com.samb1232.urfu_java_bot.dto;

public class UserMessage {
    private final long chatId;
    private final String text;
    private final TGUser tgUser;
    
    public UserMessage(long chatId, String text, TGUser tgUser) {
        this.chatId = chatId;
        this.text = text;
        this.tgUser = tgUser;
    }
    
    public long getChatId() {
        return chatId;
    }
    
    public String getText() {
        return text;
    }
    
    public TGUser getTGUser() {
        return tgUser;
    }
}