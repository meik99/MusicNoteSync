package at.htl_leonding.musicnotesync.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.bluetooth.BluetoothController;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothClient extends Thread{
    private static final String TAG = BluetoothClient.class.getSimpleName();

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mServer;
    private final BluetoothController mController;

    private InputStream inputStream;
    private OutputStream outputStream;

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

        connectToSocket();

        while(mSocket != null && mSocket.isConnected()){
            byte[] buffer = new byte[1024];
            int bytes;
            try {
                while (mSocket.isConnected()){
                    //TODO:
                    //Read buffer and react
                    bytes = inputStream.read(buffer);
                    Log.i(TAG, "run: received bluetooth data");
                }
            } catch (IOException e) {
                Log.i(TAG, "run: " + e.getMessage());
            }
        }
    }

    private void connectToSocket(){
        if(mSocket.isConnected() == false){
            try {
                mSocket.connect();
            } catch (IOException e) {
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    Log.i(TAG, "connectToSocket: " + e1.getMessage());
                }
            }
        }

        if(mSocket != null && mSocket.isConnected() == true){
            try {
                inputStream = mSocket.getInputStream();
                outputStream = mSocket.getOutputStream();
            } catch (IOException e) {
                Log.i(TAG, "connectToSocket: " + e.getMessage());
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
