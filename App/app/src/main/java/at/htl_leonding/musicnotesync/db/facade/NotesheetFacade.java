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
import java.util.UUID;

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
                NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME,
                NotesheetContract.NotesheetEntry.COLUMN_UUID,
                NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH
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
                NotesheetImpl note = new NotesheetImpl(cursor.getString(
                        cursor.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_UUID)
                ));
                long id = cursor.getInt(
                        cursor.getColumnIndex(NotesheetContract.NotesheetEntry._ID)
                );
                String filename =
                    cursor.getString(
                        cursor.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME)
                );
                String filepath =
                        cursor.getString(
                                cursor.getColumnIndex(
                                        NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH)
                        );

                note.setId(id);
                note.setName(filename);
                note.setPath(filepath);
                result.add(note);
            }while(cursor.moveToNext() == true);
        }

        dbHelper.closeCursor(cursor);

        return result;
    }

    public long insert(@Nullable Directory dir, @NonNull String filename){
        ContentValues cv = new ContentValues();
        DBHelper dbHelper = new DBHelper(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(dir == null){
            DirectoryFacade df = new DirectoryFacade(context);
            dir = df.getRoot();
        }

        cv.put(NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID, dir.getId());
        cv.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME, filename);
        cv.put(NotesheetContract.NotesheetEntry.COLUMN_UUID, UUID.randomUUID().toString());
        cv.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH,
                context.getFilesDir().getPath() + File.separator + filename);

        long id = db.insert(NotesheetContract.TABLE, null, cv);

        return id;
    }

    public Notesheet findById(long id){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        NotesheetImpl imp = null;
        Cursor cur = db.query(
                NotesheetContract.TABLE,
                new String[]{
                        NotesheetContract.NotesheetEntry._ID,
                        NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID,
                        NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME,
                        NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH,
                        NotesheetContract.NotesheetEntry.COLUMN_UUID
                },
                NotesheetContract.NotesheetEntry._ID + "=?",
                new String []{
                        String.valueOf(id)
                },
                null,
                null,
                null
        );

        if(cur != null && cur.moveToFirst() == true){
            DirectoryFacade df = new DirectoryFacade(context);
            String uuid = cur.getString(
                    cur.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_UUID)
            );
            String name = cur.getString(
                    cur.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME)
            );
            String path = cur.getString(
                    cur.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH)
            );
            long dirId = cur.getInt(
                    cur.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID)
            );
            Directory parent = df.findById(dirId);
            imp = new NotesheetImpl(uuid);
            imp.setId(id);
            imp.setName(name);
            imp.setPath(path);
            imp.setParent(parent);
        }
        return imp;
    }

    public Notesheet move(@NonNull Notesheet source, @NonNull Directory target){
        String query = "" +
                "Update " + NotesheetContract.TABLE +
                " Set " + NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID +
                "=" + target.getId() +
                " Where " + NotesheetContract.NotesheetEntry._ID +
                "=" + source.getId();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(query);

        return findById(source.getId());
    }

    public void delete(@NonNull Notesheet notesheet){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                NotesheetContract.TABLE,
                NotesheetContract.NotesheetEntry._ID +
                        " = ?",
                new String[]{String.valueOf(notesheet.getId())}
        );
    }
}
