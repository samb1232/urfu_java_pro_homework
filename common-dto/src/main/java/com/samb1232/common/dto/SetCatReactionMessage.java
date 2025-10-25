package com.samb1232.common.dto;

public class SetCatReactionMessage {
    private Long userId;
    private Long catId;
    private String action; // "LIKE" or "DISLIKE"

    public SetCatReactionMessage() {
    }

    public SetCatReactionMessage(Long userId, Long catId, String action) {
        this.userId = userId;
        this.catId = catId;
        this.action = action;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
