package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.BluetoothController;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;
/**
 * Created by michael on 1/7/17.
 */
public class BluetoothSendNotesheetClickListener implements View.OnClickListener {
    private Notesheet mNotesheet;
    private BluetoothController mBluetoothController;

    public BluetoothSendNotesheetClickListener(
            BluetoothController bluetoothController,
            Notesheet object) {
        mNotesheet = object;
        mBluetoothController = bluetoothController;
    }

    @Override
    public void onClick(View v) {
        mBluetoothController.sendNotesheet(mNotesheet);
    }
}
