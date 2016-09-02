package at.htl_leonding.musicnotesync.db.facade;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 12.08.16.
 */
public class NotesheetImpl implements Notesheet{
    String name;
    String uuid;
    long id;

    protected NotesheetImpl(){

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
}
