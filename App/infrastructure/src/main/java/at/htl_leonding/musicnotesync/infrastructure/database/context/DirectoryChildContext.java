package at.htl_leonding.musicnotesync.infrastructure.database.context;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryChild;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryChildImpl;
import at.htl_leonding.musicnotesync.infrastructure.database.DirectoryChildsContract;
import at.htl_leonding.musicnotesync.infrastructure.database.DirectoryContract;

/**
 * Created by mrynkiewicz on 17/03/17.
 */

public class DirectoryChildContext extends BaseContext<DirectoryChild>{
    public DirectoryChildContext(Context context) {
        super(context);
    }

    @Override
    public List<DirectoryChild> findAll() {
        List<DirectoryChild> result = new ArrayList<>();
        Cursor cursor = readableDatabase.rawQuery(
                String.format("select * from %1$s", DirectoryChildsContract.TABLE),
                null
        );

        if(cursor.moveToFirst()){
            do{
                DirectoryChildImpl directoryChild = new DirectoryChildImpl();
                directoryChild.fromCursor(cursor);
                result.add(directoryChild);
            }while (cursor.moveToNext());

            cursor.close();
        }
        return result;
    }

    public DirectoryChild update(DirectoryChild old, DirectoryChild _new){
        writeableDatabase.execSQL(
                String.format("update %1$s " +
                        "set %2$s = %3$d, " +
                        "%4$s = %5$d " +
                        "where %2$s = ? and %4$s = ?",
                        DirectoryChildsContract.TABLE,
                        DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID,
                        _new.getParentId(),
                        DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID,
                        _new.getChildId()),
                new String[]{String.valueOf(old.getParentId()), String.valueOf(old.getChildId())}
        );

        return _new;
    }

    public DirectoryChild create(Directory child, Directory parent){
        DirectoryChildImpl directoryChild = new DirectoryChildImpl();
        ContentValues contentValues = new ContentValues();

        directoryChild.setChildId(child.getId());
        directoryChild.setParentId(parent.getId());

        contentValues.put(
                DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID, parent.getId());
        contentValues.put(
                DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID, child.getId());

        writeableDatabase.insert(
                DirectoryChildsContract.TABLE,
                null,
                contentValues
        );

        return directoryChild;
    }
}
