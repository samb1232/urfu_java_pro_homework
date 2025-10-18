package com.samb1232.catservice.dto;

public class AddCatMessage {
    private Long userId;
    private String photoFileId;
    private String catName;

    public AddCatMessage() {
    }

    public AddCatMessage(Long userId, String photoFileId, String catName) {
        this.userId = userId;
        this.photoFileId = photoFileId;
        this.catName = catName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhotoFileId() {
        return photoFileId;
    }

    public void setPhotoFileId(String photoFileId) {
        this.photoFileId = photoFileId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
