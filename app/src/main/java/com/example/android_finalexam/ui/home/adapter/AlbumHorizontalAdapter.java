package com.example.android_finalexam.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.Album;
import com.example.android_finalexam.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class AlbumHorizontalAdapter extends RecyclerView.Adapter<AlbumHorizontalAdapter.ViewHolder> {

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    private final List<Album> albums = new ArrayList<>();
    private final OnAlbumClickListener listener;

    public AlbumHorizontalAdapter(OnAlbumClickListener listener) {
        this.listener = listener;
    }

    public void setAlbums(List<Album> newAlbums) {
        albums.clear();
        if (newAlbums != null) {
            albums.addAll(newAlbums);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.tvAlbumTitle.setText(album.getTitle());
        String subtitle = album.getArtistName() + (album.getReleaseDate() != null ? " • " + album.getReleaseDate() : "");
        holder.tvAlbumSubtitle.setText(subtitle);

        int coverResId = ResourceUtils.getDrawableResId(holder.itemView.getContext(), album.getCoverRes());
        holder.ivAlbumCover.setImageResource(coverResId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlbumClick(album);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAlbumCover;
        TextView tvAlbumTitle;
        TextView tvAlbumSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumCover = itemView.findViewById(R.id.ivAlbumCover);
            tvAlbumTitle = itemView.findViewById(R.id.tvAlbumTitle);
            tvAlbumSubtitle = itemView.findViewById(R.id.tvAlbumSubtitle);
        }
    }
}
