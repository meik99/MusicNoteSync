package at.htl_leonding.musicnotesync.mainactivity;

import android.content.Context;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.BaseModel;
import at.htl_leonding.musicnotesync.bluetooth.listener.ServerListenerImpl;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;
import at.htl_leonding.musicnotesync.io.Storage;
import at.htl_leonding.musicnotesync.mainactivity.adapter.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.mainactivity.listener.OpenAddDialogClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.management.ManagementOptionsClickListener;

/**
 * Created by michael on 11.08.16.
 */
public class MainModel extends BaseModel {
    private File photoFile;
    private Storage storage;
    private Object movedObject;
    private Directory currentDirectory;
    private Object objectToRename;

    public MainModel(Context context){
        storage = new Storage(context);
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(Directory currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public Object getMovedObject() {
        return movedObject;
    }

    public void setObjectToMove(Object movedObject) {
        this.movedObject = movedObject;
    }

    public void setObjectToRename(Object objectToRename) {
        this.objectToRename = objectToRename;
    }

    public Object getObjectToRename() {
        return objectToRename;
    }

}
