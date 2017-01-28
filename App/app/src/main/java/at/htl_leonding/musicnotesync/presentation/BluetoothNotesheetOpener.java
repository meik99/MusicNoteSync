package at.htl_leonding.musicnotesync.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;

/**
 * Created by michael on 1/28/17.
 */

public class BluetoothNotesheetOpener {
    private final Context mContext;

    public BluetoothNotesheetOpener(Context context){

        mContext = context;
    }

    public void openNotesheet(String uuid){
        NotesheetFacade notesheetFacade = new NotesheetFacade(mContext);
        Notesheet notesheet = notesheetFacade.findByUUID(uuid);

        if(notesheet != null){
            Intent intent = new Intent(mContext, ImageViewActivity.class);
            intent.putExtra(ImageViewActivity.EXTRA_PATH_NAME, notesheet.getPath());
            mContext.startActivity(intent);
        }else{
            AlertDialog dialog = new NotesheetNotFoundAlertDialog(mContext);
            dialog.show();
        }
    }
}
