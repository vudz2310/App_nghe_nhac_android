package com.example.android_finalexam.ui.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.Playlist;
import com.example.android_finalexam.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
        void onPlaylistMoreOptions(Playlist playlist, View anchorView);
    }

    private final List<Playlist> playlists = new ArrayList<>();
    private final OnPlaylistClickListener listener;

    public PlaylistAdapter(OnPlaylistClickListener listener) {
        this.listener = listener;
    }

    public void setPlaylists(List<Playlist> newPlaylists) {
        playlists.clear();
        if (newPlaylists != null) {
            playlists.addAll(newPlaylists);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.tvPlaylistName.setText(playlist.getName());
        String meta = "Playlist • " + playlist.getSongCount() + " bài hát";
        holder.tvPlaylistMeta.setText(meta);

        int coverResId = ResourceUtils.getDrawableResId(holder.itemView.getContext(), playlist.getCoverRes());
        holder.ivPlaylistCover.setImageResource(coverResId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistClick(playlist);
            }
        });

        holder.btnPlaylistMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistMoreOptions(playlist, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlaylistCover;
        TextView tvPlaylistName;
        TextView tvPlaylistMeta;
        ImageButton btnPlaylistMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaylistCover = itemView.findViewById(R.id.ivPlaylistCover);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvPlaylistMeta = itemView.findViewById(R.id.tvPlaylistMeta);
            btnPlaylistMore = itemView.findViewById(R.id.btnPlaylistMore);
        }
    }
}
