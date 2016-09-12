package at.htl_leonding.musicnotesync.bluetooth.deprecated.client;

import android.bluetooth.BluetoothDevice;
import android.view.View;

import at.htl_leonding.musicnotesync.bluetooth.deprecated.BluetoothController;

/**
 * Created by michael on 27.08.16.
 */
public class BluetoothEntryClickListener implements View.OnClickListener{
    private final BluetoothController mController;

    public BluetoothEntryClickListener(BluetoothController controller) {
        this.mController = controller;
    }

    @Override
    public void onClick(View v) {
        if(v != null && v.getTag() != null) {
            BluetoothDevice device = (BluetoothDevice) v.getTag();
            BluetoothClient client = new BluetoothClient(device, mController);

            if(client != null) {
                client.start();

            }
        }
    }
}
