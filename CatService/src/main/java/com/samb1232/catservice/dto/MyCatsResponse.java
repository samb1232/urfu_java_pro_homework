package com.samb1232.catservice.dto;

import java.util.List;

public class MyCatsResponse {
    private Long userId;
    private List<CatInfo> cats;

    public MyCatsResponse() {
    }

    public MyCatsResponse(Long userId, List<CatInfo> cats) {
        this.userId = userId;
        this.cats = cats;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CatInfo> getCats() {
        return cats;
    }

    public void setCats(List<CatInfo> cats) {
        this.cats = cats;
    }
}
