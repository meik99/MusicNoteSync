package at.htl_leonding.musicnotesync.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.io.IOException;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothController;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothClient extends Thread{
    private static final String TAG = BluetoothClient.class.getSimpleName();

    private final BluetoothController mController;
    private final BluetoothClientController mClientController;

    public BluetoothClient(BluetoothDevice server, BluetoothController controller){
        mController = controller;
        mClientController = new BluetoothClientController(server);
    }

    @Override
    public void run() {
        mController.cancelDiscovery();

        boolean handshakeSuccessful = mClientController.initiateHandshake();

        Log.i(TAG, "run: handshake was success: " + handshakeSuccessful);
    }

    public void cancel(){
        try {
            mClientController.closeConnection();
        } catch (IOException e) {
            Log.i(TAG, "cancel: " + e.getMessage());
        }
    }
}
