package at.htl_leonding.musicnotesync.db.facade;

import android.database.Cursor;

import java.io.File;

import at.htl_leonding.musicnotesync.db.NotesheetContract;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.io.Storage;

/**
 * Created by michael on 12.08.16.
 */
public class NotesheetImpl implements Notesheet{
    String name;
    String path;
    String uuid;
    Directory parent;
    long id;

    public NotesheetImpl(String string){
        uuid = string;
    }
    public NotesheetImpl(){}

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public Directory getParent() {
        return this.parent;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Directory directory){
        this.parent = directory;
    }

    @Override
    public File getFile() {
        return new File(this.path);
    }

    @Override
    public String getMetadata() {
        StringBuilder builder = new StringBuilder();
        builder
                .append(getUUID())
                .append(";")
                .append(getName());
        return builder.toString();
    }

    public void fromNotesheet(Notesheet notesheet){
        this.parent = notesheet.getParent();
        this.name = notesheet.getName();
        this.path = notesheet.getPath();
        this.id = notesheet.getId();
        this.uuid = notesheet.getUUID();
    }


}
