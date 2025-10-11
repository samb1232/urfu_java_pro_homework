package com.samb1232.urfu_java_bot.dto;

import java.util.List;

public class MyCatsResponse {
    private final Long chatId;
    private final List<CatInfo> cats;

    public MyCatsResponse(Long chatId, List<CatInfo> cats) {
        this.chatId = chatId;
        this.cats = cats;
    }

    public Long getChatId() {
        return chatId;
    }

    public List<CatInfo> getCats() {
        return cats;
    }
}
