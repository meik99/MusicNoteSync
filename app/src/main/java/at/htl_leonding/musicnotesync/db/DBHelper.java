/**
 * Implements methods for communicating with the database
 * Don't use SQLite-Methods anywhere but here
 */
package at.htl_leonding.musicnotesync.db;

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
        sqLiteDatabase.execSQL(DatabaseContract.CREATE);
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
        sqLiteDatabase.execSQL(DatabaseContract.DROP);
        onCreate(sqLiteDatabase);
    }

    public void dropDatabase(){
        this.getWritableDatabase().execSQL(DatabaseContract.DROP);
    }
}
