package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothClient extends Thread{
    private static final String TAG = BluetoothClient.class.getSimpleName();

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mServer;
    private final BluetoothController mController;

    public BluetoothClient(BluetoothDevice server, BluetoothController controller){
        BluetoothSocket socket = null;

        try {
            socket = server.createRfcommSocketToServiceRecord(BluetoothConstants.CONNECTION_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mServer = server;
        mSocket = socket;
        mController = controller;
    }

    @Override
    public void run() {
        mController.cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException e) {
            Log.i(TAG, "run: " + e.getMessage());
            try {
                mSocket.close();
            } catch (IOException e1) {
                Log.i(TAG, "run: " + e1.getMessage());
            }
        }
    }

    public void cancel(){
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.i(TAG, "cancel: " + e.getMessage());
        }
    }
}
