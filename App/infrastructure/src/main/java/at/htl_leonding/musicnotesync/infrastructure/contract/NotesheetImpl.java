package at.htl_leonding.musicnotesync.infrastructure.contract;

import android.database.Cursor;

import java.io.File;
import java.util.UUID;

import at.htl_leonding.musicnotesync.infrastructure.database.NotesheetContract;

/**
 * Created by michael on 12.08.16.
 */
public class NotesheetImpl implements Notesheet{
    private static final long serialVersionUID = 1L;

    String name;
    String path;
    String uuid;
    long parent;
    long id;

    public NotesheetImpl(String uuid){
        this.uuid = uuid;
    }
    public NotesheetImpl(){}

    public void generateUUID(){
        uuid = UUID.randomUUID().toString();
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
    public long getParentId() {
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

    public void setParentId(long directory){
        this.parent = directory;
    }

//    @Override
//    public File getFile() {
//        return new File(this.path);
//    }

    @Override
    public String getMetadata() {
        StringBuilder builder = new StringBuilder();
        builder
                .append(Notesheet.class.getSimpleName())
                .append(";")
                .append(getUUID())
                .append(";")
                .append(getName());
        return builder.toString();
    }

    public void fromNotesheet(Notesheet notesheet){
        this.parent = notesheet.getParentId();
        this.name = notesheet.getName();
        this.path = notesheet.getPath();
        this.id = notesheet.getId();
        this.uuid = notesheet.getUUID();
    }

    public void fromCursor(Cursor cursor){
        String uuid = cursor.getString(
                cursor.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_UUID)
        );
        long id = cursor.getInt(
                cursor.getColumnIndex(NotesheetContract.NotesheetEntry._ID)
        );
        String filename =
                cursor.getString(
                        cursor.getColumnIndex(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME)
                );
        String filepath =
                cursor.getString(
                        cursor.getColumnIndex(
                                NotesheetContract.NotesheetEntry.COLUMN_FILE_PATH)
                );
        long parentId =
                cursor.getLong(
                        cursor.getColumnIndex(
                                NotesheetContract.NotesheetEntry.COLUMN_DIRECTORY_ID
                        )
                );

        this.setId(id);
        this.setName(filename);
        this.setPath(filepath);
        this.setParentId(parentId);
        this.uuid = uuid;
    }


}
