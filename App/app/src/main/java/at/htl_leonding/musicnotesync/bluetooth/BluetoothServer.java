package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothServer extends Thread{
    private static final String TAG = BluetoothServer.class.getSimpleName();

    private final BluetoothServerSocket mServerSocket;
    private final List<BluetoothSocket> mClients;

    private boolean mRunning;

    public BluetoothServer(BluetoothAdapter bluetoothAdapter){
        BluetoothServerSocket serverSocket = null;

        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    bluetoothAdapter.getName(), BluetoothConstants.CONNECTION_UUID);
        } catch (IOException e) {
            Log.i(TAG, "BluetoothServer: " + e.getMessage());
        }

        mServerSocket = serverSocket;
        mClients = new LinkedList<>();
        mRunning = true;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;

        while(mRunning == true){
            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                Log.i(TAG, "run: " + e.getMessage());
            }

            if(socket != null){
                mClients.add(socket);
            }
        }
    }

    public void cancel(){
        mRunning = false;
        try {
            mServerSocket.close();
        } catch (IOException e) {
            Log.i(TAG, "run: " + e.getMessage());
        }
    }
}
