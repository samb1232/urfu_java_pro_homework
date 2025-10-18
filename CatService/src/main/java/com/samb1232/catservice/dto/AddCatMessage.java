package com.samb1232.catservice.dto;

public class AddCatMessage {
    private Long userId;
    private String photoBase64;
    private String catName;

    public AddCatMessage() {
    }

    public AddCatMessage(Long userId, String photoBase64, String catName) {
        this.userId = userId;
        this.photoBase64 = photoBase64;
        this.catName = catName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
