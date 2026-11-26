package com.example.android_finalexam.player;

import com.example.android_finalexam.domain.model.SongItem;

public class PlaybackState {
    private PlaybackStatus status = PlaybackStatus.IDLE;
    private SongItem currentSong = null;
    private long currentPositionMs = 0;
    private long durationMs = 0;
    private boolean isShuffleEnabled = false;
    private int repeatMode = 0; // 0: Off, 1: All, 2: One

    public PlaybackState() {}

    public PlaybackStatus getStatus() { return status; }
    public void setStatus(PlaybackStatus status) { this.status = status; }

    public SongItem getCurrentSong() { return currentSong; }
    public void setCurrentSong(SongItem currentSong) { this.currentSong = currentSong; }

    public long getCurrentPositionMs() { return currentPositionMs; }
    public void setCurrentPositionMs(long currentPositionMs) { this.currentPositionMs = currentPositionMs; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

    public boolean isShuffleEnabled() { return isShuffleEnabled; }
    public void setShuffleEnabled(boolean shuffleEnabled) { isShuffleEnabled = shuffleEnabled; }

    public int getRepeatMode() { return repeatMode; }
    public void setRepeatMode(int repeatMode) { this.repeatMode = repeatMode; }

    public boolean isPlaying() { return status == PlaybackStatus.PLAYING; }
}
