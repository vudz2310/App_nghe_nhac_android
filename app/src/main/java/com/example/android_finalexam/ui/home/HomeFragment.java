package com.example.android_finalexam.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.Album;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.ui.common.adapter.SongListAdapter;
import com.example.android_finalexam.ui.home.adapter.AlbumHorizontalAdapter;
import com.example.android_finalexam.ui.home.adapter.HorizontalSongAdapter;
import com.example.android_finalexam.ui.main.AppMainActivity;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements
        HorizontalSongAdapter.OnHorizontalSongClickListener,
        SongListAdapter.OnSongClickListener,
        AlbumHorizontalAdapter.OnAlbumClickListener {

    private TextView tvHomeGreeting;
    private RecyclerView rvRecentlyPlayed;
    private RecyclerView rvFeaturedSongs;
    private RecyclerView rvAlbums;

    private HorizontalSongAdapter recentlyPlayedAdapter;
    private SongListAdapter featuredSongsAdapter;
    private AlbumHorizontalAdapter albumsAdapter;

    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHomeGreeting = view.findViewById(R.id.tvHomeGreeting);
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed);
        rvFeaturedSongs = view.findViewById(R.id.rvFeaturedSongs);
        rvAlbums = view.findViewById(R.id.rvAlbums);

        updateGreeting();
        setupRecyclerViews();

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observeViewModel();
    }

    private void updateGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            tvHomeGreeting.setText("Chào buổi sáng! 🌅");
        } else if (hour >= 12 && hour < 18) {
            tvHomeGreeting.setText("Chào buổi chiều! ☀️");
        } else {
            tvHomeGreeting.setText("Chào buổi tối! 🌙");
        }
    }

    private void setupRecyclerViews() {
        // 1. Recently Played (Horizontal)
        recentlyPlayedAdapter = new HorizontalSongAdapter(this);
        rvRecentlyPlayed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRecentlyPlayed.setAdapter(recentlyPlayedAdapter);

        // 2. Featured Songs (Vertical)
        featuredSongsAdapter = new SongListAdapter(this);
        rvFeaturedSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFeaturedSongs.setAdapter(featuredSongsAdapter);

        // 3. Popular Albums (Horizontal)
        albumsAdapter = new AlbumHorizontalAdapter(this);
        rvAlbums.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAlbums.setAdapter(albumsAdapter);
    }

    private void observeViewModel() {
        viewModel.getRecentlyPlayed().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                recentlyPlayedAdapter.setSongs(songs);
            }
        });

        viewModel.getFeaturedSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                featuredSongsAdapter.setSongs(songs);
            }
        });

        viewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> {
            if (albums != null) {
                albumsAdapter.setAlbums(albums);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadAll();
        }
    }

    // Callbacks from Adapters
    @Override
    public void onSongClick(SongItem song, int position, List<SongItem> allSongs) {
        if (getActivity() instanceof AppMainActivity) {
            ((AppMainActivity) getActivity()).playSongList(allSongs, position);
        }
    }

    @Override
    public void onSongLikeToggle(SongItem song, int position) {
        viewModel.toggleLike(song, () -> featuredSongsAdapter.notifyItemChanged(position));
    }

    @Override
    public void onSongMoreOptions(SongItem song, int position, View anchorView) {
        if (getActivity() instanceof AppMainActivity) {
            ((AppMainActivity) getActivity()).showSongOptionsMenu(song, anchorView);
        }
    }

    @Override
    public void onAlbumClick(Album album) {
        Toast.makeText(getContext(), "Album: " + album.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
