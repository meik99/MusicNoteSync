package at.htl_leonding.musicnotesync.bluetooth.socket;

import android.bluetooth.BluetoothSocket;
import android.util.Base64;
import android.util.Base64InputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 1/8/17.
 */

public class SocketWatcher implements Runnable{
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
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new Base64InputStream(
                                        inputStream, Base64.DEFAULT
                                )
                        )
                );
                try {
                    String line = reader.readLine();
                    if(line != null){
                        notifyOnMessageReceived(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
