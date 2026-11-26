package com.example.android_finalexam.domain.model;

import java.io.Serializable;

public class Playlist implements Serializable {
    private int id;
    private String name;
    private String description;
    private String coverRes;
    private int songCount;
    private long createdAt;
    private long updatedAt;

    public Playlist() {}

    public Playlist(int id, String name, String description, String coverRes, int songCount, long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.coverRes = coverRes;
        this.songCount = songCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverRes() { return coverRes; }
    public void setCoverRes(String coverRes) { this.coverRes = coverRes; }

    public int getSongCount() { return songCount; }
    public void setSongCount(int songCount) { this.songCount = songCount; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
