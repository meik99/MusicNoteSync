package at.htl_leonding.musicnotesync.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

    private final List<BluetoothSocket> mClients;
    private final BluetoothAdapter mBluetoothAdapter;

    private boolean mRunning;
    private BluetoothServerSocket mServerSocket;

    public BluetoothServer(BluetoothAdapter bluetoothAdapter){
        mBluetoothAdapter = bluetoothAdapter;
        mServerSocket = this.createServerSocket();
        mClients = new LinkedList<>();
        mRunning = true;
    }

    private BluetoothServerSocket createServerSocket(){
        BluetoothServerSocket serverSocket = null;

        try {
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    mBluetoothAdapter.getName(), BluetoothConstants.CONNECTION_UUID );
        } catch (IOException e) {
            Log.i(TAG, "BluetoothServer: " + e.getMessage());
        }

        return serverSocket;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        mRunning = true;

        while(mRunning == true){
            try {
                if(mServerSocket != null) {
                    socket = mServerSocket.accept();
                }else{
                    mServerSocket = this.createServerSocket();
                }
                Log.i(TAG, "run: Started bluetooth server");
            } catch (IOException e) {
                Log.i(TAG, "run: " + e.getMessage());
            }

            if(socket != null){
                mClients.add(socket);
                try {
                    byte[] buffer = "handshake".getBytes();

                    if(socket.getOutputStream() != null){
                        socket.getOutputStream().write(buffer);
                        Log.i(TAG, "run: Wrote to stream");
                    }

                    else{
                        if(socket != null) {
                            socket.connect();
                        }
                    }
                } catch (IOException e) {
                    Log.i(TAG, "run: " + e.getMessage());
                }
            }
        }
    }

    public void cancel(){
        mRunning = false;
        try {
            if(mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            Log.i(TAG, "run: " + e.getMessage());
        }
    }
}
