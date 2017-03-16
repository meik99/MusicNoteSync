package at.htl_leonding.musicnotesync.management.move;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;
import at.htl_leonding.musicnotesync.management.move.listener.NotesheetClickListener;

/**
 * Created by michael on 11/29/16.
 */

public class MoveModel {
    private Object selectedObject;
    private Directory targetDirectory;
    private NotesheetArrayAdapter notesheetArrayAdapter;
    private NotesheetClickListener notesheetClickListener;
    private NotesheetContext notesheetFacade;
    private DirectoryContext directoryFacade;
    private Directory currentDirectory;

    protected MoveModel(){
    }

    public NotesheetContext getNotesheetFacade() {
        return notesheetFacade;
    }

    public void createFacades(Context context) {
        this.notesheetFacade = new NotesheetContext(context);
        this.directoryFacade = new DirectoryContext(context);
    }

    public DirectoryContext getDirectoryFacade() {
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
