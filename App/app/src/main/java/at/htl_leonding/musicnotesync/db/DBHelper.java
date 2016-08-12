/**
 * Implements methods for communicating with the database
 * Don't use SQLite-Methods anywhere but here
 */
package at.htl_leonding.musicnotesync.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by michael on 06.07.16.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, DatabaseContract.DATABASE, null, DatabaseContract.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DirectoryContract.CREATE);
        sqLiteDatabase.execSQL(NotesheetContract.CREATE);
        sqLiteDatabase.execSQL(DirectoryChildsContract.CREATE);

        ContentValues cvs = new ContentValues();
        cvs.put(DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME, "ROOT");

        sqLiteDatabase.insert(DirectoryContract.TABLE, null, cvs);

        cvs = new ContentValues();
        cvs.put(DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID, -1);
        cvs.put(DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID, -1);

        sqLiteDatabase.insert(DirectoryChildsContract.TABLE, null, cvs);
    }

    /**
     * Drops all tables and data in database and
     * moves to onCreate
     * @param sqLiteDatabase SQLiteDatabase instance
     * @param oldVersion Old version of database
     * @param newVersion New version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DirectoryContract.DROP);
        sqLiteDatabase.execSQL(NotesheetContract.DROP);
        sqLiteDatabase.execSQL(DirectoryChildsContract.DROP);
        onCreate(sqLiteDatabase);
    }

    public void dropDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DirectoryContract.DROP);
        db.execSQL(NotesheetContract.DROP);
        db.execSQL(DirectoryChildsContract.DROP);
    }
}
