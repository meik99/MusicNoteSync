package at.htl_leonding.musicnotesync.db.facade;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
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

    public Directory findById(long id){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DirectoryImpl imp = null;
        String query = "select * from " + DirectoryContract.TABLE +
                " left outer join " + DirectoryChildsContract.TABLE +
                " on " + DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID +
                " = " + id +
                " where " + DirectoryContract.DirectoryEntry._ID +
                "=" + id;
        Cursor cur = db.rawQuery(query, null);

        if(cur.moveToFirst() == true){
            imp = new DirectoryImpl();
            int parentId = cur.getInt(
                                cur.getColumnIndex(
                                    DirectoryChildsContract
                                            .DirectoryChildsEntry.COLUMN_PARENT_ID));
            imp.fromCursor(cur);

            if(imp.getId() != parentId){
                Directory parent = findById(parentId);
                imp.setParent(
                        parent
                );
            }else {
                Directory root = getRoot();
                imp.setParent(root);
            }
        }

        return imp;
    }

    public Directory create(String name){
        DirectoryImpl result = new DirectoryImpl();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues dirValues = new ContentValues();
        ContentValues dirChildValues = new ContentValues();
        Directory root = getRoot();
        long newId = -1;

        dirValues.put(DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME, name);

        newId = db.insert(DirectoryContract.TABLE, null, dirValues);

        if(newId == -1) {
            return null;
        }

        dirChildValues.put(DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID, newId);
        dirChildValues.put(
                DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID, root.getId());

        long chilEntryId = db.insert(DirectoryChildsContract.TABLE, null, dirChildValues);

        if(chilEntryId == -1){
            db.delete(
                    DirectoryContract.TABLE,
                    "" + DirectoryContract.DirectoryEntry._ID +
                        " = " + newId, null);
            return null;
        }

        return findById(newId);
    }

    public void delete(Directory directory) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(
                DirectoryContract.TABLE,
                "" +
                    DirectoryContract.DirectoryEntry._ID +
                    "=" + directory.getId(),
                null
        );
        db.delete(
                DirectoryChildsContract.TABLE,
                "" +
                        DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID +
                        "=" +
                        directory.getId() +
                        " or " +
                        DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID +
                        " = " +
                        directory.getId(),
                null
        );
    }
}
