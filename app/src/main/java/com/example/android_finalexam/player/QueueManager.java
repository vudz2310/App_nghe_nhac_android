package com.example.android_finalexam.player;

import android.content.Context;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.example.android_finalexam.domain.engine.SongUriResolver;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class QueueManager {

    private final List<SongItem> queue = new ArrayList<>();
    private int currentIndex = -1;

    public void setQueue(List<SongItem> songs, int startIndex) {
        queue.clear();
        if (songs != null) {
            queue.addAll(songs);
        }
        if (queue.isEmpty()) {
            currentIndex = -1;
        } else {
            currentIndex = Math.max(0, Math.min(startIndex, queue.size() - 1));
        }
    }

    public List<SongItem> getQueue() {
        return new ArrayList<>(queue);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int index) {
        if (index >= 0 && index < queue.size()) {
            this.currentIndex = index;
        }
    }

    public SongItem getCurrentSong() {
        if (currentIndex >= 0 && currentIndex < queue.size()) {
            return queue.get(currentIndex);
        }
        return null;
    }

    public SongItem getNextSong(boolean isRepeatAll, boolean isRepeatOne) {
        if (queue.isEmpty() || currentIndex < 0) return null;
        if (isRepeatOne) return queue.get(currentIndex);

        int nextIndex = currentIndex + 1;
        if (nextIndex >= queue.size()) {
            if (isRepeatAll) {
                currentIndex = 0;
                return queue.get(0);
            }
            return null;
        } else {
            currentIndex = nextIndex;
            return queue.get(currentIndex);
        }
    }

    public SongItem getPreviousSong() {
        if (queue.isEmpty() || currentIndex < 0) return null;
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            currentIndex = queue.size() - 1;
        } else {
            currentIndex = prevIndex;
        }
        return queue.get(currentIndex);
    }

    public static List<MediaItem> toMediaItems(Context context, List<SongItem> songs) {
        List<MediaItem> mediaItems = new ArrayList<>();
        if (songs == null) return mediaItems;

        for (SongItem song : songs) {
            Uri audioUri = SongUriResolver.resolveAudioUri(context, song);
            if (audioUri != null) {
                MediaMetadata metadata = new MediaMetadata.Builder()
                        .setTitle(song.getTitle())
                        .setArtist(song.getArtistName())
                        .setDescription(song.getAlbumTitle())
                        .build();

                MediaItem item = new MediaItem.Builder()
                        .setMediaId(String.valueOf(song.getId()))
                        .setUri(audioUri)
                        .setMediaMetadata(metadata)
                        .build();
                mediaItems.add(item);
            }
        }
        return mediaItems;
    }
}
