package com.example.android_finalexam.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android_finalexam.data.repository.SongRepository;
import com.example.android_finalexam.domain.model.Album;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final SongRepository songRepository;

    private final MutableLiveData<List<SongItem>> recentlyPlayed = new MutableLiveData<>();
    private final MutableLiveData<List<SongItem>> featuredSongs = new MutableLiveData<>();
    private final MutableLiveData<List<Album>> albums = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.songRepository = new SongRepository(application);
        loadAll();
    }

    public LiveData<List<SongItem>> getRecentlyPlayed() {
        return recentlyPlayed;
    }

    public LiveData<List<SongItem>> getFeaturedSongs() {
        return featuredSongs;
    }

    public LiveData<List<Album>> getAlbums() {
        return albums;
    }

    public void loadAll() {
        songRepository.getRecentlyPlayed(6, recentlyPlayed::setValue);
        songRepository.getAllSongs(featuredSongs::setValue);
        songRepository.getAllAlbums(albums::setValue);
    }

    public void toggleLike(SongItem song, Runnable onComplete) {
        songRepository.toggleLike(song.getId(), isLiked -> {
            song.setLiked(isLiked);
            if (onComplete != null) onComplete.run();
        });
    }
}
