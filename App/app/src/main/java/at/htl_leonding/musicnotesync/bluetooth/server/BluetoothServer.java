package at.htl_leonding.musicnotesync.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothServer extends Thread{
    private static final String TAG = BluetoothServer.class.getSimpleName();

    private final BluetoothServerController serverController;

    private boolean mRunning;


    public BluetoothServer(BluetoothAdapter bluetoothAdapter){
        mRunning = true;
        serverController = new BluetoothServerController(bluetoothAdapter);
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        mRunning = true;

        while(mRunning == true){
            socket = serverController.waitForClient();

            if(socket != null){
                boolean handshakeSuccessful = serverController.performHandshake(socket);

                Log.i(TAG, "run: handshake was success: " + handshakeSuccessful);
            }
        }
    }

    public void cancel(){
        mRunning = false;
    }
}
