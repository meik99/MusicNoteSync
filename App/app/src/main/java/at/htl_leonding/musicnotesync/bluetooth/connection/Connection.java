package at.htl_leonding.musicnotesync.bluetooth.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 19.09.16.
 */

public class Connection implements Runnable{

    public interface ConnectionListener{
        void onMessageReceived(Connection connection, String message);
        void onConnectionClosed(Connection connection);
    }

    @Override
    public void run() {
        while(mSocket != null) {
            InputStream is = null;
            try {
                is = mSocket.getInputStream();
            } catch (IOException e) {
                Log.i(TAG, "run: " + e.getMessage());
                close();
                connectionClosed();
            }

            if (is != null) {
                try {
                    Base64InputStream base64InputStream = new Base64InputStream(is, Base64.DEFAULT);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            base64InputStream
                    ));
                    StringBuilder message = new StringBuilder();
                    String line = null;
                    is.read();

                    while ((line = reader.readLine()) != null) {
                        Log.d(TAG, "run: Received line: \n\t" + line);
                        message.append(line);
                    }

                    receivedMessage(message.toString());
                } catch (IOException e) {
                    Log.i(TAG, "run: " + e.getMessage());
                }
            }
        }
    }

    public void close() {
        if(mSocket != null){
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "close: " + e.getMessage());
            }
            mSocket = null;
        }
    }

    private static final String TAG = Connection.class.getSimpleName();

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private List<ConnectionListener> mListener;

    public Connection(BluetoothSocket socket){
        if(socket == null)
            throw new IllegalArgumentException("Socket may not be null");

        mSocket = socket;
        mDevice = mSocket.getRemoteDevice();
        mListener = new ArrayList<>();
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

    public void sendData(final String message) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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
                return null;
            }
        };
        task.execute();
    }

    public void addListener(ConnectionListener connectionListener){
        if(connectionListener != null){
            mListener.add(connectionListener);
        }
    }

    public void removeListener(ConnectionListener connectionListener){
        if(connectionListener != null){
            mListener.remove(connectionListener);
        }
    }

    private void receivedMessage(String message){
        for(ConnectionListener listener : mListener){
            listener.onMessageReceived(this, message);
        }
    }

    private void connectionClosed(){
        for(ConnectionListener listener : mListener) {
            listener.onConnectionClosed(this);
        }
    }
}
