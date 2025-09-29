package com.samb1232.urfu_java_bot.dto;


public class UserMessage {
    private final String text;
    private final TGUser tgUser;
    private final Long chatId;

    public UserMessage(String text, TGUser tgUser, Long chatId) {
        this.text = text;
        this.tgUser = tgUser;
        this.chatId = chatId;
    }
    
    public String getText() {
        return text;
    }
    
    public TGUser getTGUser() {
        return tgUser;
    }

    public Long getChatId() {
        return chatId;
    }
}