package com.samb1232.urfu_java_bot.dto;

public class GetMyCatsMessage {
    private final Long userId;

    public GetMyCatsMessage(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
