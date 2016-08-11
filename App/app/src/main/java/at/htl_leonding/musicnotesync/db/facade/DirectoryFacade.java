package at.htl_leonding.musicnotesync.db.facade;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import at.htl_leonding.musicnotesync.db.DBHelper;
import at.htl_leonding.musicnotesync.db.DatabaseContract;
import at.htl_leonding.musicnotesync.db.DirectoryContract;
import at.htl_leonding.musicnotesync.db.contract.Directory;

/**
 * Created by michael on 09.07.16.
 */
public class DirectoryFacade {
    private Context context;

    public DirectoryFacade(Context context){
        this.context = context;
    }

    public Directory getRoot(){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = new String[]{
            DirectoryContract.DirectoryEntry._ID,
            DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME
        };
        String selection =
                DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME + " like ?";
        String[] selectionArgs = new String[]{
            "ROOT"
        };

        Cursor cursor = db.query(
            DirectoryContract.TABLE,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            "asc"
        );

        if(cursor.moveToFirst() == true){
            DirectoryImpl result = new DirectoryImpl();
            result.setId(
                    cursor.getInt(
                            cursor.getColumnIndex(
                                    DirectoryContract.DirectoryEntry._ID
            )));
            result.setName(
                    cursor.getString(
                            cursor.getColumnIndex(
                                    DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME
            )));

            return result;
        }

        return null;
    }

    public List<Directory> getChildren(Directory directory){
        return null;
    }
}
