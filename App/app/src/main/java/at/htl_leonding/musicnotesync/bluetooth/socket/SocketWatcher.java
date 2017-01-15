package at.htl_leonding.musicnotesync.bluetooth.socket;

import android.bluetooth.BluetoothSocket;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 1/8/17.
 */

public class SocketWatcher implements Runnable{
    private static final String TAG = SocketWatcher.class.getSimpleName();

    public void addListener(SocketWatcherListener socketWatcherListener) {
        this.mListeners.add(socketWatcherListener);
    }

    protected interface SocketWatcherListener{
        void onMessageReceived(BluetoothSocket socket, String message);
        void onDisconnected(BluetoothSocket socket);
    }

    private List<SocketWatcherListener> mListeners;
    private BluetoothSocket mSocket;

    protected SocketWatcher(BluetoothSocket socket){
        if(socket == null){
            throw new IllegalArgumentException("socket must not be null");
        }
        mListeners = new LinkedList<>();
        mSocket = socket;
    }

    @Override
    public void run() {
        boolean disconnected = false;

        while(disconnected == false){
            InputStream inputStream = null;
            try {
                inputStream = mSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                disconnected = true;
            }

            if(inputStream == null){
                disconnected = true;
            }else{
                Base64InputStream base64InputStream =
                                new Base64InputStream(
                                        inputStream, Base64.DEFAULT
                                );
                try {
                    byte[] buffer = new byte[256];
                    int read = -1;
                    StringBuilder builder = new StringBuilder();

                    read = base64InputStream.read(buffer);

                    if(read > -1){
                        builder.append(new String(buffer, 0, read, Charset.forName("UTF-8")));
                    }

                    while(base64InputStream.available() > 0){
                        read = base64InputStream.read(buffer);
                        builder.append(new String(buffer, 0, read, Charset.forName("UTF-8")));
                    }

                    mSocket.close();
                    disconnected = true;

                    notifyOnMessageReceived(builder.toString());

                    Log.i(TAG, "run: " + builder.toString());
                } catch (IOException e) {
                    Log.i(TAG, "run: Device disconnected");
                    disconnected = true;
                }
            }
        }
        notifyOnDisconnected();
    }

    private void notifyOnMessageReceived(String message){
        for(SocketWatcherListener watcher : mListeners){
            watcher.onMessageReceived(mSocket, message);
        }
    }

    private void notifyOnDisconnected(){
        for(SocketWatcherListener watcher : mListeners){
            watcher.onDisconnected(mSocket);
        }
    }
}
