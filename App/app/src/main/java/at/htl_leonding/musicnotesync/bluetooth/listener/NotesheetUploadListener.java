package at.htl_leonding.musicnotesync.bluetooth.listener;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.BluetoothController;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.server.listener.UploadListener;

/**
 * Created by michael on 1/15/17.
 */

public class NotesheetUploadListener implements UploadListener{
    private BluetoothController mBluetoothController;

    public NotesheetUploadListener(BluetoothController bluetoothController) {
        mBluetoothController = bluetoothController;
    }

    @Override
    public void onUploadBegin() {
        mBluetoothController.showLoadingAnimation();
    }

    @Override
    public void onUploadFinished(boolean success, Notesheet notesheet) {
        if(success == true){
//            mBluetoothController.showToast(R.string.upload_notesheet_successful);
            mBluetoothController.sendNotesheetMetadata(notesheet);
        }else{
            mBluetoothController.showSnackbar(R.string.error_upload_notesheets);
        }
        mBluetoothController.stopLoadingAnimation();
    }
}
