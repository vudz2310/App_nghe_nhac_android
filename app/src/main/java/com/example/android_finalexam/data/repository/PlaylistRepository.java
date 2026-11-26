package com.example.android_finalexam.data.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.example.android_finalexam.data.local.dao.PlaylistDao;
import com.example.android_finalexam.data.local.database.MusicDatabaseHelper;
import com.example.android_finalexam.domain.model.Playlist;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistRepository {

    private final PlaylistDao playlistDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback<T> {
        void onResult(T result);
    }

    public PlaylistRepository(Context context) {
        MusicDatabaseHelper helper = MusicDatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        this.playlistDao = new PlaylistDao(db);
    }

    public void getAllPlaylists(Callback<List<Playlist>> callback) {
        executor.execute(() -> {
            List<Playlist> list = playlistDao.getAllPlaylists();
            mainHandler.post(() -> callback.onResult(list));
        });
    }

    public void getPlaylistById(int id, Callback<Playlist> callback) {
        executor.execute(() -> {
            Playlist p = playlistDao.getPlaylistById(id);
            mainHandler.post(() -> callback.onResult(p));
        });
    }

    public void getSongsInPlaylist(int playlistId, Callback<List<SongItem>> callback) {
        executor.execute(() -> {
            List<SongItem> list = playlistDao.getSongsInPlaylist(playlistId);
            mainHandler.post(() -> callback.onResult(list));
        });
    }

    public void createPlaylist(String name, String description, Callback<Long> callback) {
        executor.execute(() -> {
            long newId = playlistDao.createPlaylist(name, description, "playinglogo");
            mainHandler.post(() -> callback.onResult(newId));
        });
    }

    public void addSongToPlaylist(int playlistId, int songId, Callback<Boolean> callback) {
        executor.execute(() -> {
            boolean success = playlistDao.addSongToPlaylist(playlistId, songId);
            mainHandler.post(() -> callback.onResult(success));
        });
    }

    public void removeSongFromPlaylist(int playlistId, int songId, Callback<Boolean> callback) {
        executor.execute(() -> {
            boolean success = playlistDao.removeSongFromPlaylist(playlistId, songId);
            mainHandler.post(() -> callback.onResult(success));
        });
    }

    public void deletePlaylist(int id, Callback<Boolean> callback) {
        executor.execute(() -> {
            boolean success = playlistDao.deletePlaylist(id);
            mainHandler.post(() -> callback.onResult(success));
        });
    }
}
