package at.htl_leonding.musicnotesync.infrastructure.facade;

import android.content.Context;

import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;

/**
 * Created by mrynkiewicz on 17/03/17.
 */

public class DirectoryFacade {
    private DirectoryContext directoryContext;

    public DirectoryFacade(Context context){
        directoryContext = new DirectoryContext(context);
    }
}
