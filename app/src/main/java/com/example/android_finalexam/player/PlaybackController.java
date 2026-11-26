package com.example.android_finalexam.player;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.example.android_finalexam.domain.model.SongItem;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;

public class PlaybackController {

    public interface PlaybackStateListener {
        void onPlaybackStateChanged(PlaybackState state);
        void onPositionDiscontinuity(long currentPos, long duration);
    }

    private static volatile PlaybackController instance;

    public static synchronized PlaybackController getInstance(Context context) {
        if (instance == null) {
            instance = new PlaybackController(context.getApplicationContext());
        }
        return instance;
    }

    private final Context appContext;
    private MediaController mediaController;
    private final QueueManager queueManager = new QueueManager();
    private final PlaybackState playbackState = new PlaybackState();
    private final List<PlaybackStateListener> listeners = new ArrayList<>();

    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaController != null && mediaController.isPlaying()) {
                long pos = mediaController.getCurrentPosition();
                long dur = mediaController.getDuration();
                if (dur < 0) dur = 0;

                playbackState.setCurrentPositionMs(pos);
                playbackState.setDurationMs(dur);

                for (PlaybackStateListener l : listeners) {
                    l.onPositionDiscontinuity(pos, dur);
                }
            }
            progressHandler.postDelayed(this, 500);
        }
    };

    private PlaybackController(Context context) {
        this.appContext = context.getApplicationContext();
        initMediaController();
    }

    private void initMediaController() {
        SessionToken sessionToken = new SessionToken(appContext, new ComponentName(appContext, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture =
                new MediaController.Builder(appContext, sessionToken).buildAsync();

        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                setupPlayerListener();
                startProgressTracking();
            } catch (Exception ignored) {}
        }, MoreExecutors.directExecutor());
    }

    private void setupPlayerListener() {
        if (mediaController == null) return;

        mediaController.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                playbackState.setStatus(isPlaying ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED);
                notifyStateChanged();
            }

            @Override
            public void onPlayerError(androidx.media3.common.PlaybackException error) {
                playbackState.setStatus(PlaybackStatus.ERROR);
                notifyStateChanged();
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    playbackState.setStatus(mediaController.isPlaying() ? PlaybackStatus.PLAYING : PlaybackStatus.READY);
                    playbackState.setDurationMs(mediaController.getDuration());
                } else if (state == Player.STATE_BUFFERING) {
                    playbackState.setStatus(PlaybackStatus.BUFFERING);
                } else if (state == Player.STATE_ENDED) {
                    playbackState.setStatus(PlaybackStatus.ENDED);
                }
                notifyStateChanged();
            }

            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                if (mediaItem != null) {
                    try {
                        int songId = Integer.parseInt(mediaItem.mediaId);
                        for (int i = 0; i < queueManager.getQueue().size(); i++) {
                            if (queueManager.getQueue().get(i).getId() == songId) {
                                queueManager.setCurrentIndex(i);
                                playbackState.setCurrentSong(queueManager.getQueue().get(i));
                                break;
                            }
                        }
                    } catch (NumberFormatException ignored) {}
                }
                notifyStateChanged();
            }
        });
    }

    private void startProgressTracking() {
        progressHandler.removeCallbacks(progressRunnable);
        progressHandler.post(progressRunnable);
    }

    public void addListener(PlaybackStateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            listener.onPlaybackStateChanged(playbackState);
        }
    }

    public void removeListener(PlaybackStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyStateChanged() {
        for (PlaybackStateListener l : listeners) {
            l.onPlaybackStateChanged(playbackState);
        }
    }

    // Playback Actions
    public void playSongList(List<SongItem> songs, int startIndex) {
        queueManager.setQueue(songs, startIndex);
        SongItem selected = queueManager.getCurrentSong();
        if (selected == null) return;

        playbackState.setCurrentSong(selected);

        if (mediaController != null) {
            List<MediaItem> mediaItems = QueueManager.toMediaItems(appContext, songs);
            mediaController.setMediaItems(mediaItems, startIndex, 0);
            mediaController.prepare();
            mediaController.play();
        }
    }

    public void play() {
        if (mediaController != null) {
            mediaController.play();
        }
    }

    public void pause() {
        if (mediaController != null) {
            mediaController.pause();
        }
    }

    private long lastActionTime = 0;
    private boolean isThrottled() {
        long now = System.currentTimeMillis();
        if (now - lastActionTime < 250) {
            return true;
        }
        lastActionTime = now;
        return false;
    }

    public void togglePlayPause() {
        if (isThrottled()) return;
        if (mediaController != null) {
            if (mediaController.isPlaying()) {
                mediaController.pause();
            } else {
                mediaController.play();
            }
        }
    }

    public void seekTo(long positionMs) {
        if (mediaController != null) {
            mediaController.seekTo(positionMs);
        }
    }

    public void skipNext() {
        if (isThrottled()) return;
        if (mediaController != null && mediaController.hasNextMediaItem()) {
            mediaController.seekToNextMediaItem();
        } else if (!queueManager.getQueue().isEmpty()) {
            SongItem next = queueManager.getNextSong(true, false);
            if (next != null) {
                playSongList(queueManager.getQueue(), queueManager.getCurrentIndex());
            }
        }
    }

    public void skipPrevious() {
        if (isThrottled()) return;
        if (mediaController != null) {
            if (mediaController.getCurrentPosition() > 3000) {
                mediaController.seekTo(0);
            } else if (mediaController.hasPreviousMediaItem()) {
                mediaController.seekToPreviousMediaItem();
            } else {
                mediaController.seekTo(0);
            }
        }
    }

    public void toggleShuffle() {
        if (mediaController != null) {
            boolean current = mediaController.getShuffleModeEnabled();
            mediaController.setShuffleModeEnabled(!current);
            playbackState.setShuffleEnabled(!current);
            notifyStateChanged();
        }
    }

    public void toggleRepeatMode() {
        if (mediaController != null) {
            int current = mediaController.getRepeatMode();
            int next;
            if (current == Player.REPEAT_MODE_OFF) {
                next = Player.REPEAT_MODE_ALL;
            } else if (current == Player.REPEAT_MODE_ALL) {
                next = Player.REPEAT_MODE_ONE;
            } else {
                next = Player.REPEAT_MODE_OFF;
            }
            mediaController.setRepeatMode(next);
            playbackState.setRepeatMode(next);
            notifyStateChanged();
        }
    }

    public PlaybackState getPlaybackState() {
        return playbackState;
    }

    public List<SongItem> getQueue() {
        return queueManager.getQueue();
    }

    public int getCurrentQueueIndex() {
        return queueManager.getCurrentIndex();
    }
}
