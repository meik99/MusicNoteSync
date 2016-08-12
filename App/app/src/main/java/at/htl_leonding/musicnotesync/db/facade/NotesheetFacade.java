package at.htl_leonding.musicnotesync.db.facade;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.db.DBHelper;
import at.htl_leonding.musicnotesync.db.NotesheetContract;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 09.07.16.
 */
public class NotesheetFacade {
    private static final String TAG = NotesheetFacade.class.getSimpleName();
    private Context context;

    public NotesheetFacade(Context context){
        if(context == null) {
            throw new IllegalArgumentException("Argument 'context' must not be null!");
        }

        this.context = context;
    }

    public List<Notesheet> getNotesheets(@Nullable  Directory directory) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Notesheet> result = new LinkedList<>();

        if(directory == null){
            DirectoryFacade df = new DirectoryFacade(context);
            directory = df.getRoot();
        }

        String[] columns = new String[]{
                NotesheetContract.NotesheetEntry._ID,
                NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME
        };
        String selection = NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID + "= ?";
        String[] selectionArgs = new String[]{
                String.valueOf(directory.getId())
        };

        Cursor cursor = db.query(NotesheetContract.TABLE,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if(cursor != null && cursor.moveToFirst()){
            Log.d(TAG, "getNotesheets: found " + cursor.getCount() + " entries");

            do{
                NotesheetImpl note = new NotesheetImpl();
                long id = cursor.getInt(
                        cursor.getColumnIndex(NotesheetContract.NotesheetEntry._ID)
                );
                String filename =
                    cursor.getString(
                        cursor.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME)
                );

                note.setId(id);
                note.setName(filename);
                result.add(note);
            }while(cursor.moveToNext() == true);
        }

        return result;
    }

    public boolean insertNotesheet(@Nullable Directory dir, @NonNull String filename){
        ContentValues cv = new ContentValues();
        DBHelper dbHelper = new DBHelper(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(dir == null){
            DirectoryFacade df = new DirectoryFacade(context);
            dir = df.getRoot();
        }

        cv.put(NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID, dir.getId());
        cv.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME, filename);

        long id = db.insert(NotesheetContract.TABLE, null, cv);

        return id > -1;
    }
}
