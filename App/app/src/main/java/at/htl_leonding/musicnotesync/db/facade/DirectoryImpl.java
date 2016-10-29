package at.htl_leonding.musicnotesync.db.facade;

import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.db.DirectoryContract;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 11.08.16.
 */
public class DirectoryImpl implements Directory{
    private String name;
    private List<Directory> children;
    private List<Notesheet> notesheets;
    private Directory parent;
    private long id;

    protected DirectoryImpl(){
        children = new LinkedList<>();
        notesheets = new LinkedList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Directory> getChildren() {
        return children;
    }

    @Override
    public List<Notesheet> getNotesheets() {
        return this.notesheets;
    }

    @Override
    public Directory getParent() {
        return parent;
    }

    @Override
    public long getId(){
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void fromDirectory(Directory directory){
        this.setParent(directory.getParent());
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
