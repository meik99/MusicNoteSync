package at.htl_leonding.musicnotesync.management.move;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.management.move.listener.NotesheetClickListener;

/**
 * Created by michael on 11/29/16.
 */

public class MoveModel {
    private Object selectedObject;
    private Directory targetDirectory;
    private NotesheetArrayAdapter notesheetArrayAdapter;
    private NotesheetClickListener notesheetClickListener;
    private NotesheetFacade notesheetFacade;
    private DirectoryFacade directoryFacade;
    private Directory currentDirectory;

    protected MoveModel(){
    }

    public NotesheetFacade getNotesheetFacade() {
        return notesheetFacade;
    }

    public void createFacades(Context context) {
        this.notesheetFacade = new NotesheetFacade(context);
        this.directoryFacade = new DirectoryFacade(context);
    }

    public DirectoryFacade getDirectoryFacade() {
        return directoryFacade;
    }

    public Object getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Object selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Directory getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(Directory targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public void setNotesheetArrayAdapter(NotesheetArrayAdapter notesheetArrayAdapter) {
        this.notesheetArrayAdapter = notesheetArrayAdapter;
    }

    public NotesheetArrayAdapter getNotesheetArrayAdapter() {
        return notesheetArrayAdapter;
    }


    public void setNotesheetClickListener(NotesheetClickListener notesheetClickListener) {
        this.notesheetClickListener = notesheetClickListener;
    }

    public NotesheetClickListener getNotesheetClickListener() {
        return notesheetClickListener;
    }

    public List<Object> getNotesheetObjects(Directory directory) {
        if(notesheetFacade == null || directoryFacade == null){
            return null;
        }

        List<Object> result = new LinkedList<>();

        result.addAll(directoryFacade.getChildren(directory));
        result.addAll(notesheetFacade.findByDirectory(directory));

        return result;
    }

    public void setCurrentDirectory(Directory currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }
}
