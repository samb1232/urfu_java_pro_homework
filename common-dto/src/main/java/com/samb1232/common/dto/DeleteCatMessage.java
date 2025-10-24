package com.samb1232.common.dto;

public class DeleteCatMessage {
    private Long catId;

    public DeleteCatMessage() {
    }

    public DeleteCatMessage(Long catId) {
        this.catId = catId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }
}
