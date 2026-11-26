package com.example.android_finalexam.domain.engine;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.utils.ResourceUtils;

import java.io.File;

public class SongUriResolver {

    /**
     * Resolves the playable Uri for a given SongItem.
     * Order of resolution:
     * 1. App-specific private storage file (if exists on disk)
     * 2. Public MediaStore Content Uri (if valid URI string)
     * 3. Bundled raw resource (android.resource://)
     */
    public static Uri resolveAudioUri(Context context, SongItem song) {
        if (song == null) return null;

        // 1. Check app private file path
        if (!TextUtils.isEmpty(song.getAppFilePath())) {
            File appFile = new File(song.getAppFilePath());
            if (appFile.exists() && appFile.length() > 0) {
                return Uri.fromFile(appFile);
            }
        }

        // 2. Check public MediaStore content URI
        if (!TextUtils.isEmpty(song.getPublicContentUri())) {
            try {
                Uri contentUri = Uri.parse(song.getPublicContentUri());
                if (contentUri != null) {
                    return contentUri;
                }
            } catch (Exception ignored) {
            }
        }

        // 3. Fallback to bundled raw audio resource
        if (!TextUtils.isEmpty(song.getAudioRes()) && context != null) {
            return ResourceUtils.getRawAudioUri(context, song.getAudioRes());
        }

        return null;
    }
}
