package com.example.android_finalexam.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android_finalexam.data.repository.SongRepository;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final SongRepository songRepository;

    private final MutableLiveData<List<SongItem>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSearching = new MutableLiveData<>(false);

    public SearchViewModel(@NonNull Application application) {
        super(application);
        this.songRepository = new SongRepository(application);
        loadAllSongs();
    }

    public LiveData<List<SongItem>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsSearching() {
        return isSearching;
    }

    public void loadAllSongs() {
        isSearching.setValue(true);
        songRepository.getAllSongs(songs -> {
            isSearching.setValue(false);
            searchResults.setValue(songs);
        });
    }

    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadAllSongs();
            return;
        }
        isSearching.setValue(true);
        songRepository.searchSongs(query, songs -> {
            isSearching.setValue(false);
            searchResults.setValue(songs);
        });
    }

    public void toggleLike(SongItem song, Runnable onComplete) {
        songRepository.toggleLike(song.getId(), isLiked -> {
            song.setLiked(isLiked);
            if (onComplete != null) onComplete.run();
        });
    }
}
