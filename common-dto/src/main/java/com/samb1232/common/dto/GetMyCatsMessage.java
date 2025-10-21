package com.samb1232.common.dto;

public class GetMyCatsMessage {
    private Long userId;

    public GetMyCatsMessage() {
    }

    public GetMyCatsMessage(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
