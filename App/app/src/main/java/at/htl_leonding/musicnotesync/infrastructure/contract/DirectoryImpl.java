package at.htl_leonding.musicnotesync.infrastructure.contract;

import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.database.DirectoryContract;

/**
 * Created by michael on 11.08.16.
 */
public class DirectoryImpl implements Directory{
    private static final long serialVersionUID = 1L;

    private String name;
    private long id;

    public DirectoryImpl(){
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getId(){
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void fromDirectory(Directory directory){
        this.setName(directory.getName());
        this.setId(directory.getId());
    }

    public void fromCursor(Cursor cur) {
        if(cur != null){
            setId(
                    cur.getInt(cur.getColumnIndex(DirectoryContract.DirectoryEntry._ID))
            );
            setName(
                    cur.getString(
                            cur.getColumnIndex(DirectoryContract.DirectoryEntry.COLUMN_DIR_NAME))
            );
        }
    }


}
