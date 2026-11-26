package com.example.android_finalexam.data.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.example.android_finalexam.data.local.dao.AlbumDao;
import com.example.android_finalexam.data.local.dao.ArtistDao;
import com.example.android_finalexam.data.local.dao.HistoryDao;
import com.example.android_finalexam.data.local.dao.LikedSongDao;
import com.example.android_finalexam.data.local.dao.SongDao;
import com.example.android_finalexam.data.local.database.MusicDatabaseHelper;
import com.example.android_finalexam.domain.model.Album;
import com.example.android_finalexam.domain.model.Artist;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SongRepository {

    private final SongDao songDao;
    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final LikedSongDao likedSongDao;
    private final HistoryDao historyDao;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback<T> {
        void onResult(T result);
    }

    public SongRepository(Context context) {
        MusicDatabaseHelper helper = MusicDatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        this.songDao = new SongDao(db);
        this.artistDao = new ArtistDao(db);
        this.albumDao = new AlbumDao(db);
        this.likedSongDao = new LikedSongDao(db);
        this.historyDao = new HistoryDao(db);
    }

    public void getAllSongs(Callback<List<SongItem>> callback) {
        executor.execute(() -> {
            List<SongItem> songs = songDao.getAllSongs();
            mainHandler.post(() -> callback.onResult(songs));
        });
    }

    public void searchSongs(String query, Callback<List<SongItem>> callback) {
        executor.execute(() -> {
            List<SongItem> songs = songDao.searchSongs(query);
            mainHandler.post(() -> callback.onResult(songs));
        });
    }

    public void getAllAlbums(Callback<List<Album>> callback) {
        executor.execute(() -> {
            List<Album> albums = albumDao.getAllAlbums();
            mainHandler.post(() -> callback.onResult(albums));
        });
    }

    public void getAllArtists(Callback<List<Artist>> callback) {
        executor.execute(() -> {
            List<Artist> artists = artistDao.getAllArtists();
            mainHandler.post(() -> callback.onResult(artists));
        });
    }

    public void getRecentlyPlayed(int limit, Callback<List<SongItem>> callback) {
        executor.execute(() -> {
            List<SongItem> recent = historyDao.getRecentlyPlayedSongs(limit);
            if (recent.isEmpty()) {
                // Fallback to sample songs if no history yet
                recent = songDao.getAllSongs();
                if (recent.size() > limit) {
                    recent = recent.subList(0, limit);
                }
            }
            final List<SongItem> finalRecent = recent;
            mainHandler.post(() -> callback.onResult(finalRecent));
        });
    }

    public void toggleLike(int songId, Callback<Boolean> callback) {
        executor.execute(() -> {
            boolean isLiked = likedSongDao.toggleLike(songId);
            mainHandler.post(() -> callback.onResult(isLiked));
        });
    }

    public void getLikedSongs(Callback<List<SongItem>> callback) {
        executor.execute(() -> {
            List<SongItem> liked = likedSongDao.getLikedSongs();
            mainHandler.post(() -> callback.onResult(liked));
        });
    }

    public void recordHistory(int songId, int progressMs) {
        executor.execute(() -> historyDao.recordHistory(songId, progressMs));
    }
}
