package at.htl_leonding.musicnotesync.infrastructure.facade;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.Entity;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.infrastructure.contract.NotesheetImpl;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;
import at.htl_leonding.musicnotesync.infrastructure.server.context.NotesheetServerContext;

/**
 * Created by mrynkiewicz on 17/03/17.
 */

public class NotesheetFacade {
    private NotesheetContext notesheetContext;
    private NotesheetServerContext notesheetServerContext;

    public NotesheetFacade(Context context){
        notesheetContext = new NotesheetContext(context);
        notesheetServerContext = new NotesheetServerContext(context);
    }


    public Notesheet create(File photo, Directory currentDirectory) {
        NotesheetImpl newNotesheet = new NotesheetImpl();
        newNotesheet.generateUUID();
        newNotesheet.setName(photo.getName());
        newNotesheet.setPath(photo.getPath());
        newNotesheet.setParentId(currentDirectory.getId());

        return notesheetContext.create(newNotesheet);
    }

    public List<Notesheet> findByDirectory(Directory parentDirectory) {
        List<Notesheet> notesheets = notesheetContext.findAll();
        List<Notesheet> result = new ArrayList<>();

        for (Notesheet notesheet :
                notesheets) {
            if (notesheet.getParentId() == parentDirectory.getId()){
                result.add(notesheet);
            }
        }

        return result;
    }

    public Notesheet update(Notesheet notesheet) {
        return notesheetContext.update(notesheet);
    }

    public Notesheet delete(Notesheet notesheet) {
        return notesheetContext.delete(notesheet);
    }

    public Notesheet findById(long id) {
        return notesheetContext.findById(id);
    }

    public Notesheet move(Notesheet notesheet, Directory targetDirectory) {
        NotesheetImpl notesheetImpl = new NotesheetImpl();
        notesheetImpl.fromNotesheet(notesheet);
        notesheetImpl.setParentId(targetDirectory.getId());
        return notesheetContext.update(notesheetImpl);
    }

    public Notesheet downloadNotesheet(String uuid, String filename, Directory activeDirectory) {
        Notesheet result = notesheetContext.findByUUID(uuid);

        if(result == null){
            File file = notesheetServerContext.download(uuid, filename);
            long id = activeDirectory == null ? 1 : activeDirectory.getId();

            if(file != null){
                NotesheetImpl notesheet = new NotesheetImpl(uuid);
                notesheet.setParentId(id);
                notesheet.setName(filename);
                notesheet.setPath(file.getPath());
                result = notesheetContext.create(notesheet);
            }
        }

        return result;
    }

    public Notesheet findByUUID(String uuid) {
        return notesheetContext.findByUUID(uuid);
    }

    public void upload(Notesheet notesheet) {
        notesheetServerContext.upload(notesheet);
    }
}
