package com.samb1232.urfu_java_bot.dto;

public class CatInfo {
    private Long id;
    private String name;
    private String photoPath;
    private Long likes;
    private Long dislikes;

    public CatInfo() {
    }

    public CatInfo(Long id, String name, String photoPath, Long likes, Long dislikes) {
        this.id = id;
        this.name = name;
        this.photoPath = photoPath;
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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
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
