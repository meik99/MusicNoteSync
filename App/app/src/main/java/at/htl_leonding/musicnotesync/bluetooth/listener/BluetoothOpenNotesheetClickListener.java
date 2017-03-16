package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.view.View;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothController;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;

/**
 * Created by michael on 1/28/17.
 */
public class BluetoothOpenNotesheetClickListener implements View.OnClickListener {
    private final BluetoothController mBluetoothController;
    private final Notesheet mNotesheet;

    public BluetoothOpenNotesheetClickListener(
            BluetoothController bluetoothController,
            Notesheet notesheet) {
        mBluetoothController = bluetoothController;
        mNotesheet = notesheet;
    }

    @Override
    public void onClick(View v) {
        mBluetoothController.openNotesheet(mNotesheet);
    }
}
