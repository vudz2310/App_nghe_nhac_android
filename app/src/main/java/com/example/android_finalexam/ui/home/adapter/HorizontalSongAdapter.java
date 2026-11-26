package com.example.android_finalexam.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class HorizontalSongAdapter extends RecyclerView.Adapter<HorizontalSongAdapter.ViewHolder> {

    public interface OnHorizontalSongClickListener {
        void onSongClick(SongItem song, int position, List<SongItem> allSongs);
    }

    private final List<SongItem> songs = new ArrayList<>();
    private final OnHorizontalSongClickListener listener;

    public HorizontalSongAdapter(OnHorizontalSongClickListener listener) {
        this.listener = listener;
    }

    public void setSongs(List<SongItem> newSongs) {
        songs.clear();
        if (newSongs != null) {
            songs.addAll(newSongs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongItem song = songs.get(position);
        holder.tvHoriTitle.setText(song.getTitle());
        holder.tvHoriArtist.setText(song.getArtistName());

        int coverResId = ResourceUtils.getDrawableResId(holder.itemView.getContext(), song.getCoverRes());
        holder.ivHoriCover.setImageResource(coverResId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song, position, songs);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHoriCover;
        TextView tvHoriTitle;
        TextView tvHoriArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHoriCover = itemView.findViewById(R.id.ivHoriCover);
            tvHoriTitle = itemView.findViewById(R.id.tvHoriTitle);
            tvHoriArtist = itemView.findViewById(R.id.tvHoriArtist);
        }
    }
}
