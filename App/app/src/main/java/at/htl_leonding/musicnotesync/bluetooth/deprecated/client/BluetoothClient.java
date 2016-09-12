package at.htl_leonding.musicnotesync.bluetooth.deprecated.client;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.io.IOException;

import at.htl_leonding.musicnotesync.bluetooth.deprecated.BluetoothController;
import at.htl_leonding.musicnotesync.bluetooth.deprecated.communication.Flag;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothClient extends Thread{
    private static final String TAG = BluetoothClient.class.getSimpleName();

    private final BluetoothController mController;
    private final BluetoothClientController mClientController;

    private boolean mRunning;

    public BluetoothClient(BluetoothDevice server, BluetoothController controller){
        mController = controller;
        mClientController = new BluetoothClientController(server);
        mRunning = false;
    }

    @Override
    public void run() {
        mRunning = true;
        mController.cancelDiscovery();

        boolean handshakeSuccessful = mClientController.initiateHandshake();

        if(handshakeSuccessful == true){
            byte[] buffer = new byte[4];
            while (mRunning == true){
                try {
                    Flag flag = mClientController.awaitFlag();
                    if(flag != null){
                        switch (flag){
                            case CONNECT:
                                mClientController.sendAcknowledge();
                            break;

                            case FILE:
                                mClientController.receiveNotesheet();
                            break;
                        }
                    }
                } catch (IOException e) {
                    Log.i(TAG, "run: " + e.getMessage());
                }
            }
        }

        Log.i(TAG, "run: handshake was success: " + handshakeSuccessful);
    }

    public void cancel(){
        try {
            mClientController.closeConnection();
            mRunning = false;
        } catch (IOException e) {
            Log.i(TAG, "cancel: " + e.getMessage());
        }
    }
}
