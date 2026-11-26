package com.example.android_finalexam.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.android_finalexam.R;

import java.io.File;

public class ResourceUtils {

    public static int getDrawableResId(Context context, String resourceName) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            return R.drawable.playinglogo;
        }
        try {
            int resId = context.getResources().getIdentifier(resourceName.trim(), "drawable", context.getPackageName());
            return resId != 0 ? resId : R.drawable.playinglogo;
        } catch (Exception e) {
            return R.drawable.playinglogo;
        }
    }

    public static void loadCover(Context context, String coverRes, ImageView imageView) {
        if (imageView == null) return;
        if (context == null || TextUtils.isEmpty(coverRes)) {
            imageView.setImageResource(R.drawable.playinglogo);
            return;
        }

        if (coverRes.startsWith("/") || coverRes.startsWith("file://")) {
            String filePath = coverRes.startsWith("file://") ? coverRes.substring(7) : coverRes;
            File file = new File(filePath);
            if (file.exists() && file.length() > 0) {
                Glide.with(context)
                        .load(file)
                        .placeholder(R.drawable.playinglogo)
                        .error(R.drawable.playinglogo)
                        .into(imageView);
                return;
            }
        }

        int resId = getDrawableResId(context, coverRes);
        imageView.setImageResource(resId);
    }

    public static int getRawAudioResId(Context context, String audioName) {
        if (audioName == null || audioName.trim().isEmpty()) {
            return 0;
        }
        try {
            return context.getResources().getIdentifier(audioName.trim(), "raw", context.getPackageName());
        } catch (Exception e) {
            return 0;
        }
    }

    public static Uri getRawAudioUri(Context context, String audioName) {
        int resId = getRawAudioResId(context, audioName);
        if (resId != 0) {
            return Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
        }
        return null;
    }

    public static String formatDuration(int durationMs) {
        if (durationMs <= 0) return "00:00";
        int totalSeconds = durationMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
