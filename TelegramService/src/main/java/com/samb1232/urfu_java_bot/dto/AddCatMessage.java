package com.samb1232.urfu_java_bot.dto;

public class AddCatMessage {
    private final Long userId;
    private final String photoFileId;
    private final String catName;

    public AddCatMessage(Long userId, String photoFileId, String catName) {
        this.userId = userId;
        this.photoFileId = photoFileId;
        this.catName = catName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPhotoFileId() {
        return photoFileId;
    }

    public String getCatName() {
        return catName;
    }
}


