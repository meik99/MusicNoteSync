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
import java.util.ArrayList;
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
public class NotesheetContext extends BaseContext<Notesheet>{

    private static final String TAG = NotesheetContext.class.getSimpleName();

    public NotesheetContext(Context context){
        super(context);
    }

    @Override
    public List<Notesheet> findAll() {
        List<Notesheet> notesheetList = new ArrayList<>();
        Cursor cursor = readableDatabase.rawQuery(
                "select * from " + NotesheetContract.TABLE,
                null
        );

        if(cursor.moveToFirst()){
            do {
                NotesheetImpl notesheet = new NotesheetImpl();
                notesheet.fromCursor(cursor);
                notesheetList.add(notesheet);
            }while(cursor.moveToNext());

            cursor.close();
        }

        return notesheetList;
    }

    @Override
    public Notesheet findById(long id) {
        Notesheet result = null;
        Cursor cursor = readableDatabase.rawQuery(
                "select * from " + NotesheetContract.TABLE +
                " where id = ?",
                new String[]{String.valueOf(id)}
        );

        if(cursor.moveToFirst()){
            NotesheetImpl notesheet = new NotesheetImpl();
            notesheet.fromCursor(cursor);
            result = notesheet;

            cursor.close();
        }

        return result;
    }

    @Override
    public Notesheet create(Notesheet entity) {
        ContentValues contentValues = new ContentValues();
        Cursor cursor = null;
        long row;
        Notesheet result = null;

        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID, entity.getParentId());
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME, entity.getName());
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH, entity.getPath());
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_UUID, entity.getUUID());

        row = writeableDatabase.insert(
                NotesheetContract.TABLE,
                null,
                contentValues
        );

        if(row > 0){
            cursor = readableDatabase.rawQuery(
                    "select * from " + NotesheetContract.TABLE +
                    " where rowid = ?",
                    new String[]{String.valueOf(row)}
            );
            if(cursor.moveToFirst()){
                NotesheetImpl notesheet = new NotesheetImpl();
                notesheet.fromCursor(cursor);
                result = notesheet;

                cursor.close();
            }
        }
        return result;
    }

    @Override
    public Notesheet update(Notesheet entity) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID, entity.getParentId());
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME, entity.getName());
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH, entity.getPath());
        contentValues.put(NotesheetContract.NotesheetEntry.COLUMN_UUID, entity.getUUID());

        writeableDatabase.update(
                NotesheetContract.TABLE,
                contentValues,
                NotesheetContract.NotesheetEntry._ID + " = ?",
                new String[]{String.valueOf(entity.getId())}
        );

        return findById(entity.getId());
    }

    @Override
    public Notesheet delete(Notesheet entity) {
        writeableDatabase.delete(
                NotesheetContract.TABLE,
                NotesheetContract.NotesheetEntry._ID + " = ?",
                new String[]{String.valueOf(entity.getId())}
        );

        return entity;
    }


    public Notesheet findByUUID(String uuid) {
        NotesheetImpl result = null;

        Cursor cursor = this.readableDatabase.rawQuery(
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

            cursor.close();
        }

        return result;
    }


}
