package com.samb1232.urfu_java_bot.dto;

public class CatInfo {
    private final String photo;
    private final String name;
    private final int likesCount;
    private final int dislikesCount;

    public CatInfo(String photo, String name, int likesCount, int dislikesCount) {
        this.photo = photo;
        this.name = name;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }
}
