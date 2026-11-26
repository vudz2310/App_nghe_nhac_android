package com.example.android_finalexam.ui.importmusic;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android_finalexam.data.repository.ImportMusicRepository;
import com.example.android_finalexam.domain.model.AudioMetadata;
import com.example.android_finalexam.domain.model.SongItem;

public class ImportMusicViewModel extends AndroidViewModel {

    public enum Status {
        IDLE,
        VALIDATING,
        DOWNLOADING,
        EXTRACTING,
        REVIEW_READY,
        SAVING,
        SUCCESS,
        ERROR
    }

    public static class ImportUiState {
        private final Status status;
        private final String message;
        private final int progressPercent;
        private final long bytesRead;
        private final long totalBytes;
        private final AudioMetadata metadata;
        private final SongItem savedSong;

        public ImportUiState(Status status, String message, int progressPercent, long bytesRead, long totalBytes, AudioMetadata metadata, SongItem savedSong) {
            this.status = status;
            this.message = message;
            this.progressPercent = progressPercent;
            this.bytesRead = bytesRead;
            this.totalBytes = totalBytes;
            this.metadata = metadata;
            this.savedSong = savedSong;
        }

        public static ImportUiState idle() {
            return new ImportUiState(Status.IDLE, "", 0, 0, 0, null, null);
        }

        public static ImportUiState loading(Status status, String message, int progress, long bytesRead, long totalBytes) {
            return new ImportUiState(status, message, progress, bytesRead, totalBytes, null, null);
        }

        public static ImportUiState reviewReady(AudioMetadata metadata) {
            return new ImportUiState(Status.REVIEW_READY, "Đã trích xuất thông tin bài hát", 100, 0, 0, metadata, null);
        }

        public static ImportUiState success(SongItem song) {
            return new ImportUiState(Status.SUCCESS, "Đã lưu vào thư viện thành công!", 100, 0, 0, null, song);
        }

        public static ImportUiState error(String message) {
            return new ImportUiState(Status.ERROR, message, 0, 0, 0, null, null);
        }

        public Status getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public int getProgressPercent() {
            return progressPercent;
        }

        public long getBytesRead() {
            return bytesRead;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public AudioMetadata getMetadata() {
            return metadata;
        }

        public SongItem getSavedSong() {
            return savedSong;
        }
    }

    private final ImportMusicRepository repository;
    private final MutableLiveData<ImportUiState> stateLiveData = new MutableLiveData<>(ImportUiState.idle());
    private AudioMetadata currentMetadata;

    public ImportMusicViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ImportMusicRepository(application);
    }

    public LiveData<ImportUiState> getState() {
        return stateLiveData;
    }

    public AudioMetadata getCurrentMetadata() {
        return currentMetadata;
    }

    public void startDownloadAndExtraction(String url) {
        stateLiveData.postValue(ImportUiState.loading(Status.VALIDATING, "Đang kiểm tra bảo mật liên kết...", 0, 0, 0));

        repository.startDownloadAndExtraction(url, (bytesRead, totalBytes, percent) -> {
            stateLiveData.postValue(ImportUiState.loading(Status.DOWNLOADING, "Đang tải xuống... " + percent + "%", percent, bytesRead, totalBytes));
        }, new ImportMusicRepository.OnMetadataExtractedCallback() {
            @Override
            public void onSuccess(AudioMetadata metadata) {
                currentMetadata = metadata;
                stateLiveData.postValue(ImportUiState.reviewReady(metadata));
            }

            @Override
            public void onError(String message) {
                stateLiveData.postValue(ImportUiState.error(message));
            }
        });
    }

    public void confirmAndSave(AudioMetadata metadata, String storageMode) {
        stateLiveData.postValue(ImportUiState.loading(Status.SAVING, "Đang lưu bài hát vào thiết bị...", 100, 0, 0));

        repository.confirmAndSave(metadata, storageMode, new ImportMusicRepository.OnSaveCompletedCallback() {
            @Override
            public void onSuccess(SongItem savedSong) {
                stateLiveData.postValue(ImportUiState.success(savedSong));
            }

            @Override
            public void onError(String message) {
                stateLiveData.postValue(ImportUiState.error(message));
            }
        });
    }

    public void cancel() {
        repository.cancelDownload();
        stateLiveData.postValue(ImportUiState.idle());
    }

    public void reset() {
        currentMetadata = null;
        stateLiveData.postValue(ImportUiState.idle());
    }
}
