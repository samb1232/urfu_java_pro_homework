package com.samb1232.catservice.dto;

public class AddCatMessage {
    private Long userId;
    private String photoPath;
    private String catName;

    public AddCatMessage() {
    }

    public AddCatMessage(Long userId, String photoPath, String catName) {
        this.userId = userId;
        this.photoPath = photoPath;
        this.catName = catName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
