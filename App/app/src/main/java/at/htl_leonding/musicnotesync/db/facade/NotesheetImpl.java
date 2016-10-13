package at.htl_leonding.musicnotesync.db.facade;

import java.io.File;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 12.08.16.
 */
public class NotesheetImpl implements Notesheet{
    String name;
    String path;
    String uuid;
    long id;
    int size;

    protected NotesheetImpl(String string){
        uuid = string;
    }

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
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public File getFile() {
        return new File(this.path);
    }
}
