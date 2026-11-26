package com.example.android_finalexam.ui.player;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_finalexam.R;
import com.example.android_finalexam.player.PlaybackController;

public class QueueDialog extends Dialog {

    private final PlaybackController playbackController;
    private QueueAdapter adapter;

    public QueueDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        this.playbackController = PlaybackController.getInstance(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_queue);

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvSubtitle = findViewById(R.id.tvQueueSubtitle);
        RecyclerView rvQueue = findViewById(R.id.rvQueueItems);
        Button btnClose = findViewById(R.id.btnCloseQueue);

        adapter = new QueueAdapter(position -> {
            playbackController.playSongList(playbackController.getQueue(), position);
            dismiss();
        });

        rvQueue.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQueue.setAdapter(adapter);

        adapter.setData(playbackController.getQueue(), playbackController.getCurrentQueueIndex());

        int count = playbackController.getQueue().size();
        tvSubtitle.setText(count + " bài hát trong hàng đợi");

        btnClose.setOnClickListener(v -> dismiss());
    }
}
