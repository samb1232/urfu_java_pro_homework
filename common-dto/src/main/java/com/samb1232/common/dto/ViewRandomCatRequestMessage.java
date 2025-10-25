package com.samb1232.common.dto;

public class ViewRandomCatRequestMessage {
    private Long userId;

    public ViewRandomCatRequestMessage() {
    }

    public ViewRandomCatRequestMessage(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
