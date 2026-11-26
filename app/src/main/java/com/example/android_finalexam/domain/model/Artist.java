package com.example.android_finalexam.domain.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private int id;
    private String name;
    private String avatarRes;
    private String bio;
    private long createdAt;

    public Artist() {}

    public Artist(int id, String name, String avatarRes, String bio, long createdAt) {
        this.id = id;
        this.name = name;
        this.avatarRes = avatarRes;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatarRes() { return avatarRes; }
    public void setAvatarRes(String avatarRes) { this.avatarRes = avatarRes; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
