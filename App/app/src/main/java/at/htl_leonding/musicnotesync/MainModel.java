package at.htl_leonding.musicnotesync;

import android.content.Context;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.io.Storage;
import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.management.ManagementOptionsClickListener;

/**
 * Created by michael on 11.08.16.
 */
public class MainModel {

    private FabOnClickListener fabOnClickListener;
    private File photoFile;
    private NotesheetFacade notesheetFacade;
    private DirectoryFacade directoryFacade;
    private Storage storage;
    private NotesheetArrayAdapter notesheetArrayAdapter;
    private Object movedObject;
    private Directory currentDirectory;
    private NotesheetClickListener notesheetItemClickListener;
    private Object objectToRename;
    private ManagementOptionsClickListener managementOptionsClickListener;

    public MainModel(Context context, MainController mainController){
        notesheetFacade = new NotesheetFacade(context);
        directoryFacade = new DirectoryFacade(context);
        storage = new Storage(context);
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public FabOnClickListener getFabOnClickListener() {
        return fabOnClickListener;
    }

    public void setFabOnClickListener(FabOnClickListener fabOnClickListener) {
        this.fabOnClickListener = fabOnClickListener;
    }

    public NotesheetFacade getNotesheetFacade() {
        return notesheetFacade;
    }

    public void setNotesheetFacade(NotesheetFacade notesheetFacade) {
        this.notesheetFacade = notesheetFacade;
    }

    public DirectoryFacade getDirectoryFacade() {
        return directoryFacade;
    }

    public void setDirectoryFacade(DirectoryFacade directoryFacade) {
        this.directoryFacade = directoryFacade;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public NotesheetArrayAdapter getNotesheetArrayAdapter() {
        return notesheetArrayAdapter;
    }

    public void setNotesheetArrayAdapter(NotesheetArrayAdapter notesheetArrayAdapter) {
        this.notesheetArrayAdapter = notesheetArrayAdapter;
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

    public List<Object> getDirectoryChildren(Directory directory) {
        List<Object> directoryChildren = new LinkedList<>();
        directoryChildren.addAll(getDirectoryFacade().getChildren(directory));
        directoryChildren.addAll(getNotesheetFacade().findByDirectory(directory));
        return directoryChildren;
    }

    public void setNotesheetItemClickListener(NotesheetClickListener notesheetItemClickListener) {
        this.notesheetItemClickListener = notesheetItemClickListener;
    }

    public NotesheetClickListener getNotesheetItemClickListener() {
        return notesheetItemClickListener;
    }

    public void setObjectToRename(Object objectToRename) {
        this.objectToRename = objectToRename;
    }

    public Object getObjectToRename() {
        return objectToRename;
    }

    public void setManagementOptionsClickListener(ManagementOptionsClickListener managementOptionsClickListener) {
        this.managementOptionsClickListener = managementOptionsClickListener;
    }

    public ManagementOptionsClickListener getManagementOptionClickListener() {
        return managementOptionsClickListener;
    }
}
