package at.htl_leonding.musicnotesync.db.facade;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.db.DBHelper;
import at.htl_leonding.musicnotesync.db.DatabaseContract;
import at.htl_leonding.musicnotesync.db.DirectoryChildsContract;
import at.htl_leonding.musicnotesync.db.DirectoryContract;
import at.htl_leonding.musicnotesync.db.NotesheetContract;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 09.07.16.
 */
public class DirectoryFacade {
    private static final String TAG = DirectoryFacade.class.getSimpleName();
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
            null
        );

        if(cursor.moveToFirst() == true){
            DirectoryImpl result = new DirectoryImpl();
            List<Directory> children;
            List<Notesheet> notesheets;
            NotesheetFacade nf = new NotesheetFacade(context);

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

            children = getChildren(result);
            notesheets = nf.getNotesheets(result);

            for(Directory dir : children){
                result.getChildren().add(dir);
            }

            for(Notesheet note : notesheets){
                result.getNotesheets().add(note);
            }

            result.setParent(null);

            return result;
        }

        dbHelper.closeCursor(cursor);
        db.close();

        return null;
    }



    public List<Directory> getChildren(@NonNull  Directory directory){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query =
                "select * from " + DirectoryContract.TABLE + ", " + DirectoryChildsContract.TABLE
                + " where " + DirectoryContract.DirectoryEntry._ID
                + " = " + DirectoryChildsContract.TABLE + "."  +
                        DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID
                + " AND " + DirectoryChildsContract.TABLE + "."  +
                        DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID
                + " = " + directory.getId();

        Cursor cursor = null;
        try {

            cursor = db.rawQuery(query, null);


            if (cursor != null && cursor.moveToFirst() == true) {
                Log.d(TAG, "getChildren: Found " + cursor.getCount() + " entries");
            }
        }catch (SQLException ex){
            Log.e(TAG, "getChildren: " + ex.getMessage());
        }
        finally {
            dbHelper.closeCursor(cursor);
        }

        db.close();

        return new LinkedList<>();
    }
}
