package at.htl_leonding.musicnotesync.bluetooth.deprecated;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import at.htl_leonding.musicnotesync.bluetooth.deprecated.BluetoothController;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothDeviceReciever extends BroadcastReceiver {
    private BluetoothController controller;

    public BluetoothDeviceReciever(BluetoothController controller){
        this.controller = controller;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(BluetoothDevice.ACTION_FOUND)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            this.controller.addDevice(device);
        }
    }
}
