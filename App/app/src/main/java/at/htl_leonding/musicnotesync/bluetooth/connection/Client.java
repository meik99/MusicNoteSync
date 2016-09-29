package at.htl_leonding.musicnotesync.bluetooth.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 26.09.16.
 */

public class Client extends Thread {
    private static final String TAG = Client.class.getSimpleName();
    private static Client instance;

    private BluetoothSocket socket;
    private boolean isRunning = false;

    private Client(){

    }

    public static Client getInstance(){
        if(instance == null){
            instance = new Client();
        }
        return instance;
    }

    public boolean connect(BluetoothDevice device){
        disconnect();
        try {
            if(socket != null){
                socket.close();
                socket = null;
            }
            socket =
                    device.
                        createRfcommSocketToServiceRecord(BluetoothConstants.CONNECTION_UUID);
            socket.connect();
            this.start();
            return true;
        } catch (IOException e) {
            Log.i(TAG, "connect: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        super.run();
        isRunning = true;

        while(isRunning == true){
            try {
                byte[] buffer = new byte[BluetoothConstants.BUFFER_FLAG_SIZE
                        + BluetoothConstants.BUFFER_CONTENT_SIZE];

                if(socket != null){
                    Log.i(TAG, "run: Socket not null");
                    InputStream is = socket.getInputStream();
                    if(is != null) {
                        Log.i(TAG, "run: InputStream not null");
                        int length = is.read(buffer);
                        BluetoothPackage receivedPackage = BluetoothPackage.fromByteArray(buffer);

                        Log.i(TAG, "run: Received Package:");
                        Log.i(TAG, "run: Flag:" + receivedPackage.getFlag().name());
                        Log.i(TAG, "run: Data:" + Arrays.toString(receivedPackage.getContent()));
                    }
                }

            } catch (IOException e) {
                Log.i(TAG, "run: " + e.getMessage());
            }
        }
    }

    public void disconnect(){
        isRunning = false;
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                Log.i(TAG, "disconnect: " + e.getMessage());
            }
            socket = null;
        }
    }
}
