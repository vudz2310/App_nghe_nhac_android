package com.example.android_finalexam.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {

    public interface OnSongClickListener {
        void onSongClick(SongItem song, int position, List<SongItem> allSongs);
        void onSongLikeToggle(SongItem song, int position);
        void onSongMoreOptions(SongItem song, int position, View anchorView);
    }

    private final List<SongItem> songs = new ArrayList<>();
    private final OnSongClickListener listener;

    public SongListAdapter(OnSongClickListener listener) {
        this.listener = listener;
    }

    public void setSongs(List<SongItem> newSongs) {
        songs.clear();
        if (newSongs != null) {
            songs.addAll(newSongs);
        }
        notifyDataSetChanged();
    }

    public List<SongItem> getSongs() {
        return songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_row, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongItem song = songs.get(position);
        holder.bind(song, position, songs, listener);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivSongCover;
        private final TextView tvSongTitle;
        private final TextView tvSongArtist;
        private final ImageButton btnRowLike;
        private final ImageButton btnRowMore;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongCover = itemView.findViewById(R.id.ivSongCover);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongArtist = itemView.findViewById(R.id.tvSongArtist);
            btnRowLike = itemView.findViewById(R.id.btnRowLike);
            btnRowMore = itemView.findViewById(R.id.btnRowMore);
        }

        public void bind(SongItem song, int position, List<SongItem> allSongs, OnSongClickListener listener) {
            tvSongTitle.setText(song.getTitle());
            tvSongArtist.setText(song.getArtistName());

            int coverResId = ResourceUtils.getDrawableResId(itemView.getContext(), song.getCoverRes());
            ivSongCover.setImageResource(coverResId);

            btnRowLike.setImageResource(song.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongClick(song, position, allSongs);
                }
            });

            btnRowLike.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongLikeToggle(song, position);
                }
            });

            btnRowMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongMoreOptions(song, position, v);
                }
            });
        }
    }
}
