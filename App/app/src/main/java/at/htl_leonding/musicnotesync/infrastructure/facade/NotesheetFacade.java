package at.htl_leonding.musicnotesync.infrastructure.facade;

import android.content.Context;

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


}
