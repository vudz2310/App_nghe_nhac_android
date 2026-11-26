package com.example.android_finalexam.ui.importmusic;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.android_finalexam.R;
import com.example.android_finalexam.domain.model.AudioMetadata;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.utils.ResourceUtils;

public class ImportMusicDialog {

    public interface OnSongImportedListener {
        void onSongImported(SongItem song);
    }

    private final Context context;
    private final ImportMusicViewModel viewModel;
    private final OnSongImportedListener listener;
    private AlertDialog dialog;

    // Input phase views
    private LinearLayout layoutInputPhase;
    private EditText edtAudioUrl;
    private Button btnPasteUrl;
    private LinearLayout layoutDownloadProgress;
    private TextView tvDownloadStatus;
    private ProgressBar pbDownload;
    private TextView tvDownloadDetails;
    private Button btnCancelInput;
    private Button btnStartDownload;

    // Review phase views
    private LinearLayout layoutReviewPhase;
    private ImageView ivReviewArtwork;
    private TextView tvReviewFileName;
    private TextView tvReviewTechInfo;
    private TextView tvReviewChecksum;
    private EditText edtReviewTitle;
    private EditText edtReviewArtist;
    private EditText edtReviewAlbum;
    private EditText edtReviewGenre;
    private RadioGroup rgStorageMode;
    private Button btnCancelReview;
    private Button btnSaveSong;

    public ImportMusicDialog(Context context, OnSongImportedListener listener) {
        this.context = context;
        this.listener = listener;

        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ImportMusicViewModel.class);
        } else {
            this.viewModel = new ImportMusicViewModel(((Activity) context).getApplication());
        }
    }

    public void show() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_import_music, null);

        bindViews(dialogView);
        setupEvents();
        observeViewModel();

        dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        viewModel.reset();
        dialog.show();
    }

    private void bindViews(View root) {
        layoutInputPhase = root.findViewById(R.id.layoutInputPhase);
        edtAudioUrl = root.findViewById(R.id.edtAudioUrl);
        btnPasteUrl = root.findViewById(R.id.btnPasteUrl);
        layoutDownloadProgress = root.findViewById(R.id.layoutDownloadProgress);
        tvDownloadStatus = root.findViewById(R.id.tvDownloadStatus);
        pbDownload = root.findViewById(R.id.pbDownload);
        tvDownloadDetails = root.findViewById(R.id.tvDownloadDetails);
        btnCancelInput = root.findViewById(R.id.btnCancelInput);
        btnStartDownload = root.findViewById(R.id.btnStartDownload);

        layoutReviewPhase = root.findViewById(R.id.layoutReviewPhase);
        ivReviewArtwork = root.findViewById(R.id.ivReviewArtwork);
        tvReviewFileName = root.findViewById(R.id.tvReviewFileName);
        tvReviewTechInfo = root.findViewById(R.id.tvReviewTechInfo);
        tvReviewChecksum = root.findViewById(R.id.tvReviewChecksum);
        edtReviewTitle = root.findViewById(R.id.edtReviewTitle);
        edtReviewArtist = root.findViewById(R.id.edtReviewArtist);
        edtReviewAlbum = root.findViewById(R.id.edtReviewAlbum);
        edtReviewGenre = root.findViewById(R.id.edtReviewGenre);
        rgStorageMode = root.findViewById(R.id.rgStorageMode);
        btnCancelReview = root.findViewById(R.id.btnCancelReview);
        btnSaveSong = root.findViewById(R.id.btnSaveSong);
    }

    private void setupEvents() {
        btnPasteUrl.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData clipData = clipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    CharSequence text = clipData.getItemAt(0).getText();
                    if (text != null) {
                        edtAudioUrl.setText(text.toString().trim());
                        edtAudioUrl.setSelection(edtAudioUrl.getText().length());
                    }
                }
            }
        });

        btnCancelInput.setOnClickListener(v -> {
            viewModel.cancel();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        btnStartDownload.setOnClickListener(v -> {
            String url = edtAudioUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập hoặc dán liên kết tải nhạc", Toast.LENGTH_SHORT).show();
                return;
            }
            btnStartDownload.setEnabled(false);
            btnPasteUrl.setEnabled(false);
            layoutDownloadProgress.setVisibility(View.VISIBLE);
            viewModel.startDownloadAndExtraction(url);
        });

        btnCancelReview.setOnClickListener(v -> {
            viewModel.cancel();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        btnSaveSong.setOnClickListener(v -> {
            AudioMetadata current = viewModel.getCurrentMetadata();
            if (current == null) {
                Toast.makeText(context, "Lỗi: Không tìm thấy dữ liệu bài hát", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = edtReviewTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
                return;
            }

            current.setTitle(title);
            current.setArtist(edtReviewArtist.getText().toString().trim());
            current.setAlbum(edtReviewAlbum.getText().toString().trim());
            current.setGenre(edtReviewGenre.getText().toString().trim());

            String storageMode = "BOTH";
            int checkedId = rgStorageMode.getCheckedRadioButtonId();
            if (checkedId == R.id.rbStoragePrivate) {
                storageMode = "APP_PRIVATE";
            } else if (checkedId == R.id.rbStoragePublic) {
                storageMode = "PUBLIC_MEDIASTORE";
            }

            btnSaveSong.setEnabled(false);
            viewModel.confirmAndSave(current, storageMode);
        });
    }

    private void observeViewModel() {
        if (!(context instanceof LifecycleOwner)) return;
        LifecycleOwner owner = (LifecycleOwner) context;

        viewModel.getState().observe(owner, state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case IDLE:
                    layoutInputPhase.setVisibility(View.VISIBLE);
                    layoutReviewPhase.setVisibility(View.GONE);
                    layoutDownloadProgress.setVisibility(View.GONE);
                    btnStartDownload.setEnabled(true);
                    btnPasteUrl.setEnabled(true);
                    break;

                case VALIDATING:
                    layoutDownloadProgress.setVisibility(View.VISIBLE);
                    tvDownloadStatus.setText(state.getMessage());
                    pbDownload.setIndeterminate(true);
                    tvDownloadDetails.setText("Kiểm tra HTTPS và địa chỉ máy chủ...");
                    break;

                case DOWNLOADING:
                    layoutDownloadProgress.setVisibility(View.VISIBLE);
                    pbDownload.setIndeterminate(false);
                    pbDownload.setProgress(state.getProgressPercent());
                    tvDownloadStatus.setText("Đang tải xuống... " + state.getProgressPercent() + "%");
                    String downloadedMb = String.format(java.util.Locale.getDefault(), "%.1f MB", state.getBytesRead() / (1024.0 * 1024.0));
                    String totalMb = state.getTotalBytes() > 0
                            ? String.format(java.util.Locale.getDefault(), "%.1f MB", state.getTotalBytes() / (1024.0 * 1024.0))
                            : "Không rõ kích thước";
                    tvDownloadDetails.setText(downloadedMb + " / " + totalMb);
                    break;

                case REVIEW_READY:
                    layoutInputPhase.setVisibility(View.GONE);
                    layoutReviewPhase.setVisibility(View.VISIBLE);
                    populateReviewData(state.getMetadata());
                    break;

                case SAVING:
                    Toast.makeText(context, state.getMessage(), Toast.LENGTH_SHORT).show();
                    break;

                case SUCCESS:
                    Toast.makeText(context, "Đã thêm bài hát vào thư viện thành công!", Toast.LENGTH_LONG).show();
                    if (listener != null && state.getSavedSong() != null) {
                        listener.onSongImported(state.getSavedSong());
                    }
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;

                case ERROR:
                    btnStartDownload.setEnabled(true);
                    btnPasteUrl.setEnabled(true);
                    btnSaveSong.setEnabled(true);
                    layoutDownloadProgress.setVisibility(View.GONE);
                    Toast.makeText(context, "Lỗi: " + state.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void populateReviewData(AudioMetadata metadata) {
        if (metadata == null) return;

        tvReviewFileName.setText(metadata.getSuggestedFileName());

        String durationStr = ResourceUtils.formatDuration(metadata.getDurationMs());
        String sizeStr = String.format(java.util.Locale.getDefault(), "%.1f MB", metadata.getFileSize() / (1024.0 * 1024.0));
        tvReviewTechInfo.setText((metadata.getMimeType() != null ? metadata.getMimeType() : "Audio") + " • " + durationStr + " • " + sizeStr);

        if (metadata.getChecksumSha256() != null && metadata.getChecksumSha256().length() >= 12) {
            tvReviewChecksum.setText("SHA-256: " + metadata.getChecksumSha256().substring(0, 12) + "...");
        } else {
            tvReviewChecksum.setText("Nguồn: " + metadata.getSourceHost());
        }

        edtReviewTitle.setText(metadata.getTitle());
        edtReviewArtist.setText(metadata.getArtist() != null ? metadata.getArtist() : "");
        edtReviewAlbum.setText(metadata.getAlbum() != null ? metadata.getAlbum() : "");
        edtReviewGenre.setText(metadata.getGenre() != null ? metadata.getGenre() : "");

        ResourceUtils.loadCover(context, metadata.getArtworkLocalPath(), ivReviewArtwork);
    }
}
