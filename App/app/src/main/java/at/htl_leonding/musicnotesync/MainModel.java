package at.htl_leonding.musicnotesync;

import android.content.Context;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.listener.ServerListenerImpl;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;
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
    private NotesheetContext notesheetFacade;
    private DirectoryContext directoryFacade;
    private Storage storage;
    private NotesheetArrayAdapter notesheetArrayAdapter;
    private Object movedObject;
    private Directory currentDirectory;
    private NotesheetClickListener notesheetItemClickListener;
    private Object objectToRename;
    private ManagementOptionsClickListener managementOptionsClickListener;
    private ServerListenerImpl serverListener;

    public MainModel(Context context, MainController mainController){
        notesheetFacade = new NotesheetContext(context);
        directoryFacade = new DirectoryContext(context);
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

    public NotesheetContext getNotesheetFacade() {
        return notesheetFacade;
    }

    public void setNotesheetFacade(NotesheetContext notesheetFacade) {
        this.notesheetFacade = notesheetFacade;
    }

    public DirectoryContext getDirectoryFacade() {
        return directoryFacade;
    }

    public void setDirectoryFacade(DirectoryContext directoryFacade) {
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

    public void setServerListener(ServerListenerImpl serverListener) {
        this.serverListener = serverListener;
    }

    public ServerListenerImpl getServerListener() {
        return serverListener;
    }
}
