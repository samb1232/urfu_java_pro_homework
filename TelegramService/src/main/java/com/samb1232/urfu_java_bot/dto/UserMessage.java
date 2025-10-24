package com.samb1232.urfu_java_bot.dto;

import com.samb1232.common.dto.TGUser;


public class UserMessage {
    private final String text;
    private final TGUser tgUser;
    private final Long chatId;
    private final String photoFileId;

    public UserMessage(String text, TGUser tgUser, Long chatId, String photoFileId) {
        this.text = text;
        this.tgUser = tgUser;
        this.chatId = chatId;
        this.photoFileId = photoFileId;
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

    public String getPhotoFileId() {
        return photoFileId;
    }
}