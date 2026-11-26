package com.example.android_finalexam.ui.player;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    public interface OnQueueItemClickListener {
        void onItemClick(int position);
    }

    private final List<SongItem> items = new ArrayList<>();
    private int currentIndex = -1;
    private final OnQueueItemClickListener listener;

    public QueueAdapter(OnQueueItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<SongItem> songs, int currentIndex) {
        this.items.clear();
        if (songs != null) {
            this.items.addAll(songs);
        }
        this.currentIndex = currentIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongItem song = items.get(position);
        boolean isCurrent = (position == currentIndex);

        holder.tvQueueIndex.setText(String.valueOf(position + 1));
        holder.tvQueueTitle.setText(song.getTitle());
        holder.tvQueueArtist.setText(song.getArtistName());

        int coverRes = ResourceUtils.getDrawableResId(holder.itemView.getContext(), song.getCoverRes());
        holder.ivQueueCover.setImageResource(coverRes);

        int titleColor = isCurrent ?
                ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_primary) :
                ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary);
        holder.tvQueueTitle.setTextColor(titleColor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQueueIndex;
        ImageView ivQueueCover;
        TextView tvQueueTitle;
        TextView tvQueueArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQueueIndex = itemView.findViewById(R.id.tvQueueIndex);
            ivQueueCover = itemView.findViewById(R.id.ivQueueCover);
            tvQueueTitle = itemView.findViewById(R.id.tvQueueTitle);
            tvQueueArtist = itemView.findViewById(R.id.tvQueueArtist);
        }
    }
}
