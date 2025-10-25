package com.samb1232.common.dto;

public class ViewRandomCatResponseMessage {
    private Long userId;
    private Long catId;
    private String name;
    private String photoPath;
    private int likesCount;
    private int dislikesCount;

    public ViewRandomCatResponseMessage() {
    }

    public ViewRandomCatResponseMessage(Long userId, Long catId, String name, String photoPath, int likesCount, int dislikesCount) {
        this.userId = userId;
        this.catId = catId;
        this.name = name;
        this.photoPath = photoPath;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }
}
