package com.samb1232.common.dto;

public class CatInfo {
    private Long id;
    private String name;
    private String photoPath;
    private Integer likes;
    private Integer dislikes;

    public CatInfo() {
    }

    public CatInfo(Long id, String name, String photoPath, Integer likes, Integer dislikes) {
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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }
}
