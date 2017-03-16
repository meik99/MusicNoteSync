package at.htl_leonding.musicnotesync.infrastructure.database.context;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import at.htl_leonding.musicnotesync.infrastructure.database.DBHelper;
import at.htl_leonding.musicnotesync.infrastructure.database.NotesheetContract;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.infrastructure.contract.NotesheetImpl;
import at.htl_leonding.musicnotesync.io.Storage;

/**
 * Created by michael on 09.07.16.
 */
public class NotesheetContext {

    public interface NotesheetDbListener{
        void onNotesheetInserted(Notesheet notesheet);
    }

    private static final String TAG = NotesheetContext.class.getSimpleName();

    private Context mContext;
    private List<NotesheetDbListener> mListeners;

    public void addListener(NotesheetDbListener listener){
        if(listener != null)
            mListeners.add(listener);
    }

    public void removeListener(NotesheetDbListener listener){
        if(listener != null)
            mListeners.remove(listener);
    }

    public NotesheetContext(Context context){
        if(context == null) {
            throw new IllegalArgumentException("Argument 'mContext' must not be null!");
        }

        this.mContext = context;
        mListeners = new LinkedList<>();
    }


    public Notesheet findByUUID(String uuid) {
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        NotesheetImpl result = null;

        Cursor cursor = db.rawQuery(
                "select * from " + NotesheetContract.TABLE +
                        " where " + NotesheetContract.NotesheetEntry.COLUMN_UUID +
                        " = ?",
                new String[]{
                        uuid
                }
        );

        if(cursor.moveToFirst()){
            result = new NotesheetImpl();
            result.fromCursor(cursor);
        }

        return result;
    }

    public List<Notesheet> getNotesheets(@Nullable  Directory directory) {
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        List<Notesheet> result = new LinkedList<>();

        if(directory == null){
            DirectoryContext df = new DirectoryContext(mContext);
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
                NotesheetImpl note = new NotesheetImpl();
                note.fromCursor(cursor);
                result.add(note);
            }while(cursor.moveToNext() == true);
        }

        dbHelper.closeCursor(cursor);

        return result;
    }

    private void notifyInserted(Notesheet inserted){
        for (NotesheetDbListener listener :
                mListeners) {
            listener.onNotesheetInserted(inserted);
        }
    }

    public void insertFromInputStream(final byte[] bytes,
                                        final String directory,
                                        final String filename,
                                        final String uuid){
        AsyncTask<Void, Void, Notesheet> asyncTask =
                new AsyncTask<Void, Void, Notesheet>() {
            @Override
            protected Notesheet doInBackground(Void... params) {
                File bluetoothDirector =
                        new File(mContext.getFilesDir()
                                + File.separator
                                + directory
                                + File.separator);

                if(bluetoothDirector.exists() == false)
                    bluetoothDirector.mkdir();

                String path =
                        bluetoothDirector
                        + File.separator
                        + filename;
                try {
                    FileOutputStream fileOutputStream
                            = new FileOutputStream(path, false);

                    fileOutputStream.write(bytes);

                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DirectoryContext directoryFacade = new DirectoryContext(mContext);
                Notesheet inserted =
                        NotesheetContext.this
                                .insert(directoryFacade.getRoot(),
                                        "bluetooth",
                                        filename,
                                        uuid);
                Log.i(TAG, "downloadFinished: " + inserted.getMetadata());
                notifyInserted(inserted);
                return inserted;
            }
        };
        asyncTask.execute();
    }

    public Notesheet insert(@Nullable Directory dir, @NonNull String filename){
        Storage storage = new Storage(mContext);

        return insert(dir, storage.getCameraDirectory(), filename);
    }

    public Notesheet insert(Directory dir, String directoryPath, String filename){
        return insert(dir, directoryPath, filename, UUID.randomUUID().toString());
    }

    public Notesheet insert(Directory dir, String directoryPath, String filename, String uuid) {
        ContentValues cv = new ContentValues();
        DBHelper dbHelper = new DBHelper(this.mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(dir == null){
            DirectoryContext df = new DirectoryContext(mContext);
            dir = df.getRoot();
        }

        cv.put(NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID, dir.getId());
        cv.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME, filename);
        cv.put(NotesheetContract.NotesheetEntry.COLUMN_UUID, uuid);
        cv.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH,
                directoryPath + File.separator + filename);

        Notesheet inserted = null;
        try {
            long id = db.insert(NotesheetContract.TABLE, null, cv);
            inserted = findById(id);
        }catch(SQLiteConstraintException ex){

        }
        notifyInserted(inserted);
        return inserted;
    }

    public Notesheet findById(long id){
        DBHelper dbHelper = new DBHelper(mContext);
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
            DirectoryContext df = new DirectoryContext(mContext);
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
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(query);

        return findById(source.getId());
    }

    public void delete(@NonNull Notesheet notesheet){
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                NotesheetContract.TABLE,
                NotesheetContract.NotesheetEntry._ID +
                        " = ?",
                new String[]{String.valueOf(notesheet.getId())}
        );
    }

    public Notesheet update(@NonNull Notesheet notesheet){
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DirectoryContext df = new DirectoryContext(this.mContext);

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH, notesheet.getPath());
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME, notesheet.getName());

        if(notesheet.getParent() == null){
            contentValues.put(
                    NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID,
                    df.getRoot().getId());
        }
        else{
            contentValues.put(
                    NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID,
                    notesheet.getParent().getId());
        }


        db.update(
                NotesheetContract.TABLE,
                contentValues,
                NotesheetContract.NotesheetEntry._ID + "=?",
                new String[]{String.valueOf(notesheet.getId())}
        );
        return findById(notesheet.getId());
    }

    public List<Notesheet> findByDirectory(Directory parent) {
        List<Notesheet> result = new LinkedList<>();
        DirectoryContext directoryFacade = new DirectoryContext(mContext);
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        parent = parent == null ? directoryFacade.getRoot() : parent;
        cursor = db.rawQuery("SELECT * FROM " +
                NotesheetContract.TABLE +
                " WHERE " + NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID +
                "= ?",
                new String[]{
                        String.valueOf(parent.getId())
                });

        if(cursor.moveToFirst()){
            do{
                result.add(createNotesheetFromCursor(cursor));
            }while(cursor.moveToNext());
        }

        return result;
    }

    private Notesheet createNotesheetFromCursor(Cursor cursor){
        NotesheetImpl notesheet;
        DirectoryContext directoryFacade = new DirectoryContext(mContext);
        String filenameColumn = NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME;
        String filepathColumn = NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH;
        String uuidColumn = NotesheetContract.NotesheetEntry.COLUMN_UUID;
        String idColumn = NotesheetContract.NotesheetEntry._ID;
        String parentIdColumn = NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID;

        long id = cursor.getLong(cursor.getColumnIndex(idColumn));
        long parentId = cursor.getLong(cursor.getColumnIndex(parentIdColumn));
        String filename = cursor.getString(cursor.getColumnIndex(filenameColumn));
        String filepath = cursor.getString(cursor.getColumnIndex(filepathColumn));
        String uuid = cursor.getString(cursor.getColumnIndex(uuidColumn));
        Directory parent = directoryFacade.findById(parentId);

        notesheet = new NotesheetImpl(uuid);
        notesheet.setId(id);
        notesheet.setName(filename);
        notesheet.setPath(filepath);
        notesheet.setParent(parent);

        return notesheet;
    }
}
