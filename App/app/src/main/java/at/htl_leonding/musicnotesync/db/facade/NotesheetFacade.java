package at.htl_leonding.musicnotesync.db.facade;

import android.content.ContentValues;
import android.content.Context;

import java.io.File;

import at.htl_leonding.musicnotesync.db.DBHelper;

/**
 * Created by michael on 09.07.16.
 */
public class NotesheetFacade {
    private Context context;

    public NotesheetFacade(Context context){
        if(context == null) {
            throw new IllegalArgumentException("Argument 'context' must not be null!");
        }

        this.context = context;
    }

    public boolean insertNotesheet(String filename){
        ContentValues values = new ContentValues();
        DBHelper dbHelper = new DBHelper(this.context);


        return false;
    }
}
