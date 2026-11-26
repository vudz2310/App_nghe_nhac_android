package com.example.android_finalexam.ui.library;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android_finalexam.data.local.dao.LikedSongDao;
import com.example.android_finalexam.data.local.database.MusicDatabaseHelper;
import com.example.android_finalexam.data.repository.PlaylistRepository;
import com.example.android_finalexam.domain.model.Playlist;

import java.util.List;
import java.util.concurrent.Executors;

public class LibraryViewModel extends AndroidViewModel {

    private final PlaylistRepository playlistRepository;
    private final LikedSongDao likedSongDao;

    private final MutableLiveData<List<Playlist>> playlists = new MutableLiveData<>();
    private final MutableLiveData<Integer> likedSongCount = new MutableLiveData<>(0);

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        this.playlistRepository = new PlaylistRepository(application);
        this.likedSongDao = new LikedSongDao(MusicDatabaseHelper.getInstance(application).getWritableDatabase());
        loadData();
    }

    public LiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }

    public LiveData<Integer> getLikedSongCount() {
        return likedSongCount;
    }

    public void loadData() {
        playlistRepository.getAllPlaylists(playlists::setValue);
        Executors.newSingleThreadExecutor().execute(() -> {
            int count = likedSongDao.getLikedSongCount();
            likedSongCount.postValue(count);
        });
    }

    public void createPlaylist(String name, String description, Runnable onSuccess) {
        playlistRepository.createPlaylist(name, description, newId -> {
            loadData();
            if (onSuccess != null) onSuccess.run();
        });
    }

    public void deletePlaylist(int id, Runnable onSuccess) {
        playlistRepository.deletePlaylist(id, success -> {
            loadData();
            if (onSuccess != null) onSuccess.run();
        });
    }
}
