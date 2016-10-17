package at.htl_leonding.musicnotesync.bluetooth.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 26.09.16.
 */

public class Client extends Thread {
    private static final String TAG = Client.class.getSimpleName();
    private static Client instance;

    private BluetoothSocket socket = null;
    private boolean running = false;

    private Client(){
        this.start();
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

            return true;
        } catch (IOException e) {
            Log.i(TAG, "connect: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        super.run();
        running = true;
        int disconnectCount = 0;

        while(running == true){
            try {
                byte[] buffer = new byte[BluetoothConstants.BUFFER_FLAG_SIZE
                        + BluetoothConstants.BUFFER_CONTENT_SIZE];

                if(socket != null){
                    Log.i(TAG, "run: Socket not null");
                    InputStream is = socket.getInputStream();
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

                            OutputStream os = socket.getOutputStream();
                            os.write(answer.toByteArray());

                            switch (receivedPackage.getFlag()){
                                case FILE:
                                    Log.i(TAG, "run: Got file metadata");
                                    Log.i(TAG, "run: " + new String(receivedPackage.getContent()));
                                break;
                                case FILEDATA:
                                    Log.i(TAG, "run: Got file data");
                                    Log.i(TAG, "run: " + new String(receivedPackage.getContent()));
                                    break;
                            }
                        }
                        catch(IllegalArgumentException e){
                            Log.i(TAG, "run: " + e.getMessage());

                            BluetoothPackage answer = new BluetoothPackage();
                            answer.setFlag(Flag.NEGATIVE);
                            answer.setContent(new byte[0]);

                            OutputStream os = socket.getOutputStream();
                            os.write(answer.toByteArray());
                        }
                    }
                }else{
                    Thread.yield();
                }

            } catch (IOException e) {
                Log.i(TAG, "run: " + e.getMessage());
                if(disconnectCount++ > BluetoothConstants.TRY_MAX){
                    running = false;
                }
            }
        }

        disconnect();
    }

    public void disconnect(){
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
