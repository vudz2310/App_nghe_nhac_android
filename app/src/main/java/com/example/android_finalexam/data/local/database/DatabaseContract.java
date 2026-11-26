package com.example.android_finalexam.data.local.database;

public final class DatabaseContract {

    private DatabaseContract() {}

    public static final String DATABASE_NAME = "songs_database";
    public static final int DATABASE_VERSION = 4;

    // Table: artists
    public static final class ArtistEntry {
        public static final String TABLE_NAME = "artists";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AVATAR_RES = "avatar_res";
        public static final String COLUMN_BIO = "bio";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    // Table: albums
    public static final class AlbumEntry {
        public static final String TABLE_NAME = "albums";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_COVER_RES = "cover_res";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    // Table: songs
    public static final class SongEntry {
        public static final String TABLE_NAME = "songs";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_ALBUM_ID = "album_id";
        public static final String COLUMN_DURATION_MS = "duration_ms";
        public static final String COLUMN_AUDIO_RESOURCE = "audio_resource";
        public static final String COLUMN_IMAGE_RESOURCE = "image_resource";
        public static final String COLUMN_CREATED_AT = "created_at";

        // Extended columns for Import Music
        public static final String COLUMN_SOURCE_TYPE = "source_type";       // 'ASSET' or 'IMPORTED'
        public static final String COLUMN_MIME_TYPE = "mime_type";           // e.g. 'audio/mpeg'
        public static final String COLUMN_FILE_SIZE = "file_size";           // bytes
        public static final String COLUMN_FILE_NAME = "file_name";           // physical filename
        public static final String COLUMN_APP_FILE_PATH = "app_file_path";   // private filesDir path
        public static final String COLUMN_PUBLIC_CONTENT_URI = "public_content_uri"; // MediaStore URI string
        public static final String COLUMN_STORAGE_MODE = "storage_mode";     // 'APP', 'PUBLIC', 'BOTH'
        public static final String COLUMN_SOURCE_HOST = "source_host";       // normalized host
        public static final String COLUMN_CHECKSUM_SHA256 = "checksum_sha256"; // hash
    }

    // Table: playlists
    public static final class PlaylistEntry {
        public static final String TABLE_NAME = "playlists";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COVER_RES = "cover_res";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    // Table: playlist_songs
    public static final class PlaylistSongEntry {
        public static final String TABLE_NAME = "playlist_songs";
        public static final String COLUMN_PLAYLIST_ID = "playlist_id";
        public static final String COLUMN_SONG_ID = "song_id";
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_ADDED_AT = "added_at";
    }

    // Table: liked_songs
    public static final class LikedSongEntry {
        public static final String TABLE_NAME = "liked_songs";
        public static final String COLUMN_SONG_ID = "song_id";
        public static final String COLUMN_LIKED_AT = "liked_at";
    }

    // Table: listening_history
    public static final class HistoryEntry {
        public static final String TABLE_NAME = "listening_history";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SONG_ID = "song_id";
        public static final String COLUMN_PLAYED_AT = "played_at";
        public static final String COLUMN_PROGRESS_MS = "progress_ms";
    }

    // Table: import_jobs
    public static final class ImportJobEntry {
        public static final String TABLE_NAME = "import_jobs";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SOURCE_URL_HASH = "source_url_hash";
        public static final String COLUMN_FILE_NAME = "file_name";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_PROGRESS = "progress";
        public static final String COLUMN_TEMP_PATH = "temp_path";
        public static final String COLUMN_ERROR_CODE = "error_code";
        public static final String COLUMN_ERROR_MESSAGE = "error_message";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }
}
