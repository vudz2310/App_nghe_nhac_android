package com.example.android_finalexam.domain.model;

import java.io.Serializable;

public class SongItem implements Serializable {
    private int id;
    private String title;
    private int artistId;
    private String artistName;
    private Integer albumId;
    private String albumTitle;
    private int durationMs;
    private String audioRes;
    private String coverRes;
    private boolean isLiked;

    // Extended fields for Import Music
    private String sourceType = "ASSET";       // 'ASSET' or 'IMPORTED'
    private String mimeType = "audio/mpeg";
    private long fileSize = 0;
    private String fileName;
    private String appFilePath;
    private String publicContentUri;
    private String storageMode = "APP";         // 'APP', 'PUBLIC', 'BOTH'
    private String sourceHost;
    private String checksumSha256;

    public SongItem() {}

    public SongItem(int id, String title, int artistId, String artistName, Integer albumId, String albumTitle,
                    int durationMs, String audioRes, String coverRes, boolean isLiked) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.artistName = artistName;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.durationMs = durationMs;
        this.audioRes = audioRes;
        this.coverRes = coverRes;
        this.isLiked = isLiked;
        this.sourceType = "ASSET";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public String getArtistName() { return artistName != null ? artistName : "Nghệ sĩ"; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public Integer getAlbumId() { return albumId; }
    public void setAlbumId(Integer albumId) { this.albumId = albumId; }

    public String getAlbumTitle() { return albumTitle; }
    public void setAlbumTitle(String albumTitle) { this.albumTitle = albumTitle; }

    public int getDurationMs() { return durationMs; }
    public void setDurationMs(int durationMs) { this.durationMs = durationMs; }

    public String getAudioRes() { return audioRes; }
    public void setAudioRes(String audioRes) { this.audioRes = audioRes; }

    public String getCoverRes() { return coverRes; }
    public void setCoverRes(String coverRes) { this.coverRes = coverRes; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getAppFilePath() { return appFilePath; }
    public void setAppFilePath(String appFilePath) { this.appFilePath = appFilePath; }

    public String getPublicContentUri() { return publicContentUri; }
    public void setPublicContentUri(String publicContentUri) { this.publicContentUri = publicContentUri; }

    public String getStorageMode() { return storageMode; }
    public void setStorageMode(String storageMode) { this.storageMode = storageMode; }

    public String getSourceHost() { return sourceHost; }
    public void setSourceHost(String sourceHost) { this.sourceHost = sourceHost; }

    public String getChecksumSha256() { return checksumSha256; }
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    public boolean isImported() {
        return "IMPORTED".equalsIgnoreCase(sourceType);
    }
}
