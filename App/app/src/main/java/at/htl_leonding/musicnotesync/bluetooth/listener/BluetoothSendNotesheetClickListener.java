package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.view.View;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothController;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;

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
