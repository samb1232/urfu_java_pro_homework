package com.samb1232.urfu_java_bot.dto;

public class AddCatMessage {
    private final Long userId;
    private final String photoPath;
    private final String catName;

    public AddCatMessage(Long userId, String photoPath, String catName) {
        this.userId = userId;
        this.photoPath = photoPath;
        this.catName = catName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getCatName() {
        return catName;
    }
}


