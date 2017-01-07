package at.htl_leonding.musicnotesync.bluetooth;

import android.view.View;
import android.widget.Toast;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;
/**
 * Created by michael on 1/7/17.
 */
public class BluetoothSendNotesheetClickListener implements View.OnClickListener {
    private Notesheet mNotesheet;

    public BluetoothSendNotesheetClickListener(Notesheet object) {
        mNotesheet = object;
    }

    @Override
    public void onClick(View v) {
        NotesheetFacade notesheetFacade = new NotesheetFacade(v.getContext());
        boolean success = notesheetFacade.sendNotesheet(mNotesheet);

        if(success == true){
            Toast
                .makeText(v.getContext(), R.string.upload_notesheet_successful, Toast.LENGTH_SHORT)
                .show();
        }else{
            Toast
                .makeText(v.getContext(), R.string.error_upload_notesheets, Toast.LENGTH_SHORT)
                .show();
        }
    }
}
