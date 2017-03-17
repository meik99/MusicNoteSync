package at.htl_leonding.musicnotesync.infrastructure.facade;

import android.content.Context;
import android.net.Uri;

import java.io.File;

import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
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
}
