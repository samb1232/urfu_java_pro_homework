package com.samb1232.urfu_java_bot.dto;

public class CatInfo {
    private Long id;
    private String name;
    private String photoBase64;
    private Long likes;
    private Long dislikes;

    public CatInfo() {
    }

    public CatInfo(Long id, String name, String photoBase64, Long likes, Long dislikes) {
        this.id = id;
        this.name = name;
        this.photoBase64 = photoBase64;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }
}
