package at.htl_leonding.musicnotesync.db;

/**
 * Created by michael on 06.07.16.
 */
public final class DirectoryChildsContract {
    private DirectoryChildsContract(){}

    public static final String TABLE = "MNS_DIRECTORY_CHILD";
    public static final String SEPERATOR = DatabaseContract.SEPERATOR;
    public static final String CREATE = "CREATE TABLE " + TABLE + "(" +
            DirectoryChildsEntry.COLUMN_PARENT_ID + " INTEGER NOT NULL" + SEPERATOR +
            DirectoryChildsEntry.COLUMN_CHILD_ID + " INTEGER NOT NULL" + SEPERATOR +
            "FOREIGN KEY (" + DirectoryChildsEntry.COLUMN_PARENT_ID + ") REFERENCES " +
                DirectoryContract.TABLE + "(" + DirectoryContract.DirectoryEntry._ID + ")" + SEPERATOR +
            "FOREIGN KEY (" + DirectoryChildsEntry.COLUMN_CHILD_ID + ") REFERENCES " +
            DirectoryContract.TABLE + "(" + DirectoryContract.DirectoryEntry._ID + ")" +
            ");";
    public static final String DROP = "DROP TABLE IF EXISTS " + TABLE + ";";

    public static abstract class DirectoryChildsEntry{
        public static final String COLUMN_PARENT_ID = "PARENT_ID";
        public static final String COLUMN_CHILD_ID = "CHILD_ID";
    }

}
