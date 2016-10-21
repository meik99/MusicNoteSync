package at.htl_leonding.musicnotesync.bluetooth.connection.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.bluetooth.connection.BluetoothPackage;
import at.htl_leonding.musicnotesync.bluetooth.connection.Flag;

/**
 * Created by michael on 26.09.16.
 */

public class Client{
    private static final String TAG = Client.class.getSimpleName();
    private static Client instance;

    private BluetoothSocket mSocket = null;

    private Client(){}

    public static Client getInstance(){
        if(instance == null){
            instance = new Client();
        }
        return instance;
    }

    public boolean connect(BluetoothDevice device){
        disconnect();
        AsyncTask<BluetoothDevice, Void, Void> task = new AsyncTask() {
            @Override
            protected Void doInBackground(Object[] objects) {
                BluetoothDevice device = (BluetoothDevice)objects[0];
                BluetoothSocket socket;
                try {
                    socket = device.
                            createRfcommSocketToServiceRecord(
                                    BluetoothConstants.CONNECTION_UUID);
                    if(socket != null) {
                        socket.connect();
                        mSocket = socket;
                    }else{
                        mSocket = null;
                    }
                } catch (IOException e) {
                    Log.i(TAG, "doInBackground: " + e.getMessage());
                    mSocket = null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

            }
        };

        task.execute(device);
        return true;
    }

    public void run() {
        //running = true;
        int disconnectCount = 0;

        //while(running == true){
        try {
            byte[] buffer = new byte[BluetoothConstants.BUFFER_FLAG_SIZE
                    + BluetoothConstants.BUFFER_CONTENT_SIZE];

            if(mSocket != null){
                Log.i(TAG, "run: Socket not null");
                InputStream is = mSocket.getInputStream();
                if(is != null) {
                    Log.i(TAG, "run: InputStream not null");
                    int length = is.read(buffer);
                    byte[] received = ByteBuffer.wrap(buffer, 0, length).compact().array();
                    BluetoothPackage receivedPackage = null;
                    try {
                        receivedPackage = BluetoothPackage.fromByteArray(buffer);


                        Log.i(TAG, "run: Received Package:");
                        Log.i(TAG, "run: Flag:" + receivedPackage.getFlag().name());
                        Log.i(TAG, "run: Data:" + Arrays.toString(receivedPackage.getContent()));

                        BluetoothPackage answer = new BluetoothPackage();
                        answer.setFlag(Flag.POSITIVE);
                        answer.setContent(new byte[0]);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(answer.toByteArray());

                        switch (receivedPackage.getFlag()){
                            case FILE:
                                Log.i(TAG, "run: Got file metadata");
                                Log.i(TAG, "run: " + new String(receivedPackage.getContent()));

                                BufferedInputStream bis = new BufferedInputStream(is);
                                List<byte[]> bytes = new LinkedList<>();
                                received = new byte[BluetoothConstants.BUFFER_FILE_BUFFER];

                                while((length = bis.read(received)) > -1){
                                    Log.i(TAG, "run: Received buffer of length " + length);
                                    bytes.add(received);
                                    received = new byte[BluetoothConstants.BUFFER_FILE_BUFFER];
                                }

                            break;
//                                case FILEDATA:
//                                    Log.i(TAG, "run: Got file data");
//                                    Log.i(TAG, "run: " + new String(receivedPackage.getContent()));
//                                    break;
                        }
                    }
                    catch(IllegalArgumentException e){
                        Log.i(TAG, "run: " + e.getMessage());

                        BluetoothPackage answer = new BluetoothPackage();
                        answer.setFlag(Flag.NEGATIVE);
                        answer.setContent(new byte[0]);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(answer.toByteArray());
                    }
                }
            }else{
                Thread.yield();
            }

        } catch (IOException e) {
            Log.i(TAG, "run: " + e.getMessage());
            if(disconnectCount++ > BluetoothConstants.TRY_MAX){
//                    running = false;
                Log.i(TAG, "run: Socket disconnected");
            }
        }
        //}

        disconnect();
    }

    public void disconnect(){
        if(mSocket != null){
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "disconnect: " + e.getMessage());
            }
            mSocket = null;
        }
    }
}
