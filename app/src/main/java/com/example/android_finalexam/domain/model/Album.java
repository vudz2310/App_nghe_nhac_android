package com.example.android_finalexam.domain.model;

import java.io.Serializable;

public class Album implements Serializable {
    private int id;
    private String title;
    private int artistId;
    private String artistName;
    private String coverRes;
    private String releaseDate;
    private long createdAt;

    public Album() {}

    public Album(int id, String title, int artistId, String artistName, String coverRes, String releaseDate, long createdAt) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.artistName = artistName;
        this.coverRes = coverRes;
        this.releaseDate = releaseDate;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getCoverRes() { return coverRes; }
    public void setCoverRes(String coverRes) { this.coverRes = coverRes; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
