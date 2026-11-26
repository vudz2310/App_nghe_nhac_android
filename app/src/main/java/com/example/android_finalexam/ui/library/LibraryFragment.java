package com.example.android_finalexam.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.data.repository.PlaylistRepository;
import com.example.android_finalexam.data.repository.SongRepository;
import com.example.android_finalexam.domain.model.Playlist;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.ui.importmusic.ImportMusicDialog;
import com.example.android_finalexam.ui.library.adapter.PlaylistAdapter;
import com.example.android_finalexam.ui.main.AppMainActivity;

import java.util.Collections;

public class LibraryFragment extends Fragment implements PlaylistAdapter.OnPlaylistClickListener {

    private Button btnCreatePlaylist;
    private CardView cardLikedSongs;
    private CardView cardImportMusic;
    private TextView tvLikedSongsCount;
    private RecyclerView rvPlaylists;

    private PlaylistAdapter playlistAdapter;
    private LibraryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCreatePlaylist = view.findViewById(R.id.btnCreatePlaylist);
        cardLikedSongs = view.findViewById(R.id.cardLikedSongs);
        cardImportMusic = view.findViewById(R.id.cardImportMusic);
        tvLikedSongsCount = view.findViewById(R.id.tvLikedSongsCount);
        rvPlaylists = view.findViewById(R.id.rvPlaylists);

        playlistAdapter = new PlaylistAdapter(this);
        rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlaylists.setAdapter(playlistAdapter);

        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);

        viewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            if (playlists != null) {
                playlistAdapter.setPlaylists(playlists);
            }
        });

        viewModel.getLikedSongCount().observe(getViewLifecycleOwner(), count -> {
            tvLikedSongsCount.setText((count != null ? count : 0) + " bài hát");
        });

        btnCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());

        cardLikedSongs.setOnClickListener(v -> {
            // Open liked songs playlist
            new SongRepository(requireContext()).getLikedSongs(likedSongs -> {
                if (likedSongs.isEmpty()) {
                    Toast.makeText(getContext(), "Chưa có bài hát yêu thích nào", Toast.LENGTH_SHORT).show();
                } else if (getActivity() instanceof AppMainActivity) {
                    ((AppMainActivity) getActivity()).playSongList(likedSongs, 0);
                }
            });
        });

        cardImportMusic.setOnClickListener(v -> {
            new ImportMusicDialog(requireContext(), importedSong -> {
                if (getActivity() instanceof AppMainActivity && importedSong != null) {
                    ((AppMainActivity) getActivity()).playSongList(Collections.singletonList(importedSong), 0);
                }
            }).show();
        });
    }

    private void showCreatePlaylistDialog() {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_playlist, null);
        EditText edtName = dialogView.findViewById(R.id.edtPlaylistName);
        EditText edtDesc = dialogView.findViewById(R.id.edtPlaylistDesc);
        Button btnCancel = dialogView.findViewById(R.id.btnDialogCancel);
        Button btnCreate = dialogView.findViewById(R.id.btnDialogCreate);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnCreate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên danh sách phát", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.createPlaylist(name, desc, () -> {
                Toast.makeText(getContext(), "Đã tạo playlist: " + name, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadData();
        }
    }

    @Override
    public void onPlaylistClick(Playlist playlist) {
        new PlaylistRepository(requireContext()).getSongsInPlaylist(playlist.getId(), songs -> {
            if (songs.isEmpty()) {
                Toast.makeText(getContext(), "Danh sách phát trống", Toast.LENGTH_SHORT).show();
            } else if (getActivity() instanceof AppMainActivity) {
                ((AppMainActivity) getActivity()).playSongList(songs, 0);
            }
        });
    }

    @Override
    public void onPlaylistMoreOptions(Playlist playlist, View anchorView) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Tùy chọn: " + playlist.getName())
                .setItems(new String[]{"Phát tất cả", "Xóa playlist"}, (dialog, which) -> {
                    if (which == 0) {
                        onPlaylistClick(playlist);
                    } else if (which == 1) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Xóa playlist")
                                .setMessage("Bạn có chắc chắn muốn xóa playlist \"" + playlist.getName() + "\" không?")
                                .setPositiveButton("Xóa", (d, w) -> {
                                    viewModel.deletePlaylist(playlist.getId(), () ->
                                            Toast.makeText(getContext(), "Đã xóa playlist", Toast.LENGTH_SHORT).show());
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                })
                .show();
    }
}
