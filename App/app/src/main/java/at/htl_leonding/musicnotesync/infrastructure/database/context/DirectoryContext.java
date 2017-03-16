package at.htl_leonding.musicnotesync.infrastructure.database.context;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.database.DBHelper;
import at.htl_leonding.musicnotesync.infrastructure.database.DirectoryChildsContract;
import at.htl_leonding.musicnotesync.infrastructure.database.DirectoryContract;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryImpl;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;

/**
 * Created by michael on 09.07.16.
 */
public class DirectoryContext extends BaseContext<Directory>{
    private static final String TAG = DirectoryContext.class.getSimpleName();
    private static final String ROOT = "ROOT";

    public DirectoryContext(Context context){
        super(context);
    }

    @Override
    public List<Directory> findAll(){
        List<Directory> result = new ArrayList<>();

        Cursor cursor = readableDatabase.rawQuery(
                String.format("select * from %1$s;", DirectoryContract.TABLE),
                null
        );

        if(cursor.moveToFirst()){
            do {
                DirectoryImpl directory = new DirectoryImpl();
                directory.fromCursor(cursor);
                result.add(directory);
            }while (cursor.moveToNext());
        }

        return result;
    }

    @Override
    public Directory findById(long id) {
        Directory result = null;

        Cursor cursor = readableDatabase.rawQuery(
                String.format("select * from %1$s where id = ?;", DirectoryContract.TABLE),
                new String[]{String.valueOf(id)}
        );

        if(cursor.moveToFirst()){
            DirectoryImpl directory = new DirectoryImpl();
            directory.fromCursor(cursor);
            result = directory;
        }

        return result;
    }

    @Override
    public Directory create(Directory entity) {
        ContentValues contentValues = new ContentValues();
        Directory result = null;
        Cursor cursor;
        long row;

        contentValues.put(DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME, entity.getName());

        row = writeableDatabase.insert(
                DirectoryContract.TABLE,
                null,
                contentValues
        );

        if(row > -1){
            cursor = readableDatabase.rawQuery(
                    String.format("select * from %1$s where ROWID = ?;", DirectoryContract.TABLE),
                    new String[]{String.valueOf(row)}
            );

            if(cursor.moveToFirst()){
                DirectoryImpl directory = new DirectoryImpl();
                directory.fromCursor(cursor);
                result =  directory;
            }
        }

        return result;
    }

    public Directory getRoot(){
        List<Directory> directories = findAll();

        for (Directory directory :
                directories) {
            if (directory.getName().equals(ROOT)){
                return directory;
            }
        }

        DirectoryImpl directory = new DirectoryImpl();
        directory.setName(ROOT);

        return create(directory);
    }

    @Deprecated
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
        List<Directory> result = new LinkedList<>();
        try {

            cursor = db.rawQuery(query, null);


            if (cursor != null && cursor.moveToFirst() == true) {
                Log.d(TAG, "getChildren: Found " + cursor.getCount() + " entries");

                do{
                    DirectoryImpl dir = new DirectoryImpl();
                    dir.setId(cursor.getInt(
                            cursor.getColumnIndex(
                                    DirectoryContract.DirectoryEntry._ID)));
                    dir.setName(cursor.getString(
                            cursor.getColumnIndex(
                                    DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME)));
                    dir.setParent(directory);
                    result.add(dir);
                }while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Log.e(TAG, "getChildren: " + ex.getMessage());
        }
        finally {
            dbHelper.closeCursor(cursor);
        }

        db.close();

        return result;
    }

    @Deprecated
    public Directory move(Directory source, Directory target){
        Directory root = getRoot();

        if(source.equals(root)){
            throw new IllegalArgumentException("Cannot move root directory");
        }
        if(source.getId() == target.getId()){

        }

        long childId = source.getId();
        long oldParentId = source.getParent().getId();
        long newParentId = target.getId();

        String query = "update " + DirectoryChildsContract.TABLE + " " +
                "set " + DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID +
                "=" + newParentId + " " +
                "where " + DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID +
                "=" + childId +
                " and " + DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID +
                "=" + oldParentId;

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(query);

        DirectoryImpl imp = new DirectoryImpl();
        imp.fromDirectory(source);
        imp.setParent(target);

        return imp;
    }

    @Override
    public Directory delete(Directory directory) {
        writeableDatabase.delete(
                DirectoryContract.TABLE,
                "" +
                    DirectoryContract.DirectoryEntry._ID +
                    "=" + directory.getId(),
                null
        );

        return directory;
    }

    @Override
    public Directory update(Directory directory){
        ContentValues contentValues = new ContentValues();

        contentValues.put(DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME, directory.getName());
        writeableDatabase.update(
                DirectoryContract.TABLE,
                contentValues,
                DirectoryContract.DirectoryEntry._ID + " = ?",
                new String[]{String.valueOf(directory.getId())}
        );

        return findById(directory.getId());
    }

    @Deprecated
    public Directory rename(Directory directory){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME, directory.getName());
        db.update(
                DirectoryContract.TABLE,
                contentValues,
                DirectoryContract.DirectoryEntry._ID + "=?",
                new String[]{String.valueOf(directory.getId())}
        );

        return findById(directory.getId());
    }
}
