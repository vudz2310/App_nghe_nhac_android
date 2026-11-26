package com.example.android_finalexam.domain.model;

import java.io.Serializable;

public class AudioMetadata implements Serializable {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int durationMs;
    private long fileSize;
    private String mimeType;
    private String sourceHost;
    private String tempFilePath;
    private String artworkLocalPath; // Path to saved artwork image in app cache
    private String checksumSha256;
    private String suggestedFileName;

    public AudioMetadata() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDurationMs() { return durationMs; }
    public void setDurationMs(int durationMs) { this.durationMs = durationMs; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getSourceHost() { return sourceHost; }
    public void setSourceHost(String sourceHost) { this.sourceHost = sourceHost; }

    public String getTempFilePath() { return tempFilePath; }
    public void setTempFilePath(String tempFilePath) { this.tempFilePath = tempFilePath; }

    public String getArtworkLocalPath() { return artworkLocalPath; }
    public void setArtworkLocalPath(String artworkLocalPath) { this.artworkLocalPath = artworkLocalPath; }

    public String getChecksumSha256() { return checksumSha256; }
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    public String getSuggestedFileName() { return suggestedFileName; }
    public void setSuggestedFileName(String suggestedFileName) { this.suggestedFileName = suggestedFileName; }
}
