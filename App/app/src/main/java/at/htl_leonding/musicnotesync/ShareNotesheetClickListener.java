package at.htl_leonding.musicnotesync;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothActivity;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;

/**
 * Created by michael on 1/5/17.
 */
public class ShareNotesheetClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        Object item = v.getTag();
        if(item instanceof Notesheet){
            Intent openBluetoothActivity = new Intent(v.getContext(), BluetoothActivity.class);
            openBluetoothActivity.putExtra(
                    BluetoothActivity.OPERATION,
                    BluetoothActivity.SEND_NOTESHEET);
            openBluetoothActivity.putExtra (BluetoothActivity.ENTITY_ID, ((Notesheet)item).getId());
            v.getContext().startActivity(openBluetoothActivity);
//
//            NotesheetFacade notesheetFacade = new NotesheetFacade(v.getContext());
//            boolean success = notesheetFacade.sendNotesheet((Notesheet) item);
//
//            if(success == true){
//                Toast
//                        .makeText(v.getContext(), R.string.upload_notesheet_successful, Toast.LENGTH_SHORT)
//                        .show();
//            }else{
//                Toast
//                        .makeText(v.getContext(), R.string.error_upload_notesheets, Toast.LENGTH_SHORT)
//                        .show();
//            }
        }else {
            Toast
                    .makeText(v.getContext(), R.string.error_upload_notesheets_only, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
