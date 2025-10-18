package com.samb1232.urfu_java_bot.dto;

public class AddCatMessage {
    private final Long userId;
    private final String photoBase64;
    private final String catName;

    public AddCatMessage(Long userId, String photoBase64, String catName) {
        this.userId = userId;
        this.photoBase64 = photoBase64;
        this.catName = catName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public String getCatName() {
        return catName;
    }
}


