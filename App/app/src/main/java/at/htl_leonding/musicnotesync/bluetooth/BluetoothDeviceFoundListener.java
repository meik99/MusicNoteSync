package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by michael on 17.08.16.
 */
public interface BluetoothDeviceFoundListener {
    void deviceFound(BluetoothDevice device);
}
