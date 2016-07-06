package at.htl_leonding.musicnotesync.db;

import android.provider.BaseColumns;
import android.provider.ContactsContract;

import java.security.PublicKey;

/**
 * Created by michael on 06.07.16.
 */
public final class NotesheetContract {
    private NotesheetContract(){}

    public static final String TABLE = "MNS_NOTESHEET";
    public static final String SEPERATOR = DatabaseContract.SEPERATOR;
    public static final String CREATE = "CREATE TABLE " + TABLE + "(" +
            NotesheetEntry.COLUMN_DIRECTORY_ID + " INTEGER NOT NULL" + SEPERATOR +
            NotesheetEntry.COLUMN_FILE_NAME + " TEXT NOT NULL" + SEPERATOR +
            "FOREIGN KEY(" + NotesheetEntry.COLUMN_DIRECTORY_ID + ")" +
            " REFERENCES " + DirectoryContract.TABLE + "(" + DirectoryContract.DirectoryEntry._ID + ")" +
            ");";

    public static final String DROP = "DROP TABLE IF EXISTS " + TABLE + ";";

    public static abstract class NotesheetEntry implements BaseColumns{
        public static final String COLUMN_DIRECTORY_ID = "NOTE_DIR_ID";
        public static final String COLUMN_FILE_NAME = "NOTE_FILE_NAME";
    }
}
