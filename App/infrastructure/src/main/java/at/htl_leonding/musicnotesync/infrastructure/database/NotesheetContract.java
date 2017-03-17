package at.htl_leonding.musicnotesync.infrastructure.database;

import android.provider.BaseColumns;

/**
 * Created by michael on 06.07.16.
 */
public final class NotesheetContract {
    private NotesheetContract(){}

    public static final String TABLE = "MNS_NOTESHEET";
    public static final String SEPERATOR = DatabaseContract.SEPERATOR;
    public static final String CREATE = "CREATE TABLE " + TABLE + "(" +
            NotesheetEntry._ID + " INTEGER PRIMARY KEY" + SEPERATOR +
            NotesheetEntry.COLUMN_DIRECTORY_ID + " INTEGER NOT NULL" + SEPERATOR +
            NotesheetEntry.COLUMN_FILE_NAME + " TEXT NOT NULL" + SEPERATOR +
            NotesheetEntry.COLUMN_FILE_PATH + " TEXT NOT NULL" + SEPERATOR +
            NotesheetEntry.COLUMN_UUID + " TEXT NOT NULL UNIQUE" + SEPERATOR +
            "FOREIGN KEY(" + NotesheetEntry.COLUMN_DIRECTORY_ID + ")" +
            " REFERENCES " + DirectoryContract.TABLE + "(" + DirectoryContract.DirectoryEntry._ID + ")" +
            ");";

    public static final String DROP = "DROP TABLE IF EXISTS " + TABLE + ";";

    public static abstract class NotesheetEntry implements BaseColumns{
        public static final String COLUMN_DIRECTORY_ID = "NOTE_DIR_ID";
        public static final String COLUMN_FILE_NAME = "NOTE_FILE_NAME";
        public static final String COLUMN_FILE_PATH = "NOTE_FILE_PATH";
        public static final String COLUMN_UUID = "NOTE_UUID";
    }
}
