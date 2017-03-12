package at.htl_leonding.musicnotesync.blt.interfaces;

import android.bluetooth.BluetoothSocket;

/**
 * Created by michael on 3/12/17.
 */

public interface MessageReceivedListener {
    void onMessageReceived(BluetoothSocket socket, String message);
}
