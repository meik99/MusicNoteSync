package at.htl_leonding.musicnotesync.management.move;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.contract.Entity;
import at.htl_leonding.musicnotesync.infrastructure.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.infrastructure.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.mainactivity.adapter.NotesheetArrayAdapter;
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
    private NotesheetFacade notesheetFacade;
    private DirectoryFacade directoryFacade;
    private Directory currentDirectory;

    protected MoveModel(){
    }

    public void createFacades(Context context) {
        this.notesheetFacade = new NotesheetFacade(context);
        this.directoryFacade = new DirectoryFacade(context);
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

    public List<Entity> getNotesheetObjects(Directory directory) {
        if(notesheetFacade == null || directoryFacade == null){
            return null;
        }

        List<Entity> result = new LinkedList<>();

        result.addAll(directoryFacade.findByDirectory(directory));
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
