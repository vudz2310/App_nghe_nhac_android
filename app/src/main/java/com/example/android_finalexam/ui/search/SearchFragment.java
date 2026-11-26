package com.example.android_finalexam.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.ui.common.adapter.SongListAdapter;
import com.example.android_finalexam.ui.main.AppMainActivity;

import java.util.List;

public class SearchFragment extends Fragment implements SongListAdapter.OnSongClickListener {

    private EditText edtSearchInput;
    private TextView tvSearchResultsHeader;
    private RecyclerView rvSearchResults;

    private SongListAdapter searchAdapter;
    private SearchViewModel viewModel;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtSearchInput = view.findViewById(R.id.edtSearchInput);
        tvSearchResultsHeader = view.findViewById(R.id.tvSearchResultsHeader);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        searchAdapter = new SongListAdapter(this);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(searchAdapter);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                searchAdapter.setSongs(songs);
                if (songs.isEmpty()) {
                    tvSearchResultsHeader.setText("Không tìm thấy kết quả phù hợp");
                } else {
                    String query = edtSearchInput.getText().toString().trim();
                    if (query.isEmpty()) {
                        tvSearchResultsHeader.setText("Tất cả bài hát (" + songs.size() + ")");
                    } else {
                        tvSearchResultsHeader.setText("Kết quả tìm kiếm (" + songs.size() + ")");
                    }
                }
            }
        });

        edtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> viewModel.search(s.toString());
                searchHandler.postDelayed(searchRunnable, 300); // Debounce 300ms
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onSongClick(SongItem song, int position, List<SongItem> allSongs) {
        if (getActivity() instanceof AppMainActivity) {
            ((AppMainActivity) getActivity()).playSongList(allSongs, position);
        }
    }

    @Override
    public void onSongLikeToggle(SongItem song, int position) {
        viewModel.toggleLike(song, () -> searchAdapter.notifyItemChanged(position));
    }

    @Override
    public void onSongMoreOptions(SongItem song, int position, View anchorView) {
        if (getActivity() instanceof AppMainActivity) {
            ((AppMainActivity) getActivity()).showSongOptionsMenu(song, anchorView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
