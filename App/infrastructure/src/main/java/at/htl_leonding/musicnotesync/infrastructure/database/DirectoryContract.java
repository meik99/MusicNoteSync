package at.htl_leonding.musicnotesync.infrastructure.database;

import android.provider.BaseColumns;

/**
 * Created by michael on 06.07.16.
 */
public final class DirectoryContract {
    private DirectoryContract(){}

    public static final String TABLE = "MNS_DIRECTORY";
    public static final String SEPERATOR = DatabaseContract.SEPERATOR;

    public static final String CREATE = "CREATE TABLE " + TABLE + "(" +
            DirectoryEntry._ID + " INTEGER PRIMARY KEY" + SEPERATOR +
            DirectoryEntry.COLUMN_DIR_NAME + " TEXT NOT NULL CHECK(length(" +
                DirectoryEntry.COLUMN_DIR_NAME + ") > 0)"
            + ");";

    public static final String DROP = "DROP TABLE IF EXISTS " + TABLE + ";";

    public static abstract class DirectoryEntry implements BaseColumns{
        public static final String COLUMN_DIR_NAME = "DIR_NAME";
    }
}
