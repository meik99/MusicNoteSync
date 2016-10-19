package at.htl_leonding.musicnotesync.bluetooth.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by michael on 19.09.16.
 */

public class Connection{
    private static final String TAG = Connection.class.getSimpleName();

    BluetoothSocket mSocket;
    BluetoothDevice mDevice;

    public Connection(BluetoothSocket socket){
        if(socket == null)
            throw new IllegalArgumentException("Socket may not be null");

        mSocket = socket;
        mDevice = mSocket.getRemoteDevice();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Connection == false){
            return false;
        }

        Connection connection = (Connection)o;

        return mDevice
                .getAddress()
                .equals(
                        connection
                                .mDevice
                                .getAddress());
    }

    public void sendData(String message) {
        OutputStream os = null;

        try {
            os = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.i(TAG, "sendData: " + e.getMessage());
        }

        if(os != null){
            PrintStream printStream = new PrintStream(
                    new Base64OutputStream(os, Base64.DEFAULT)
            );
            printStream.print(message);
        }
    }
}
