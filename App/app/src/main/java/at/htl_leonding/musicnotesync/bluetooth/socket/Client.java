package at.htl_leonding.musicnotesync.bluetooth.socket;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 1/8/17.
 */

public class Client implements SocketWatcher.SocketWatcherListener {

    public interface ClientListener{
        void onClientConnected(BluetoothSocket socket);
        void onClientMessageReceived(BluetoothSocket socket, String message);
        void onClientDisconnected(BluetoothSocket socket);
    }

    private Executor mExecutor;
    private List<ClientListener> mListener;
    private BluetoothSocket mSocket;

    public Client(){
        mListener = new LinkedList<>();
        mExecutor = BluetoothExecutor.BLUETOOTH_EXECUTOR;
    }

    public void connect(BluetoothDevice device){
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(
                    BluetoothConstants.CONNECTION_UUID
            );
            socket.connect();
            if(socket != null){
                mSocket = socket;
                SocketWatcher socketWatcher = new SocketWatcher(mSocket);
                socketWatcher.addListener(this);
                notifyOnConnected(mSocket);
                mExecutor.execute(socketWatcher);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onDisconnected(mSocket);
    }

    public void sendMessage(String message){
        try {
            if(mSocket != null && mSocket.getOutputStream() != null) {
                Base64OutputStream base64OutputStream =
                                new Base64OutputStream(
                                        mSocket.getOutputStream(), Base64.DEFAULT
                                );

                base64OutputStream.write(message.getBytes());
                base64OutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void notifyOnConnected(BluetoothSocket socket){
        for(ClientListener listener : mListener){
            listener.onClientConnected(socket);
        }
    }

    @Override
    public void onMessageReceived(BluetoothSocket socket, String message) {
        for(ClientListener listener : mListener) {
            listener.onClientMessageReceived(socket, message);
        }
    }

    @Override
    public void onDisconnected(BluetoothSocket socket) {
        for(ClientListener listener : mListener){
            listener.onClientDisconnected(socket);
        }
    }

    public void addListener(ClientListener listener){
        if(mListener.contains(listener) == false){
            mListener.add(listener);
        }
    }

    public void removeListener(ClientListener listener){
        mListener.remove(listener);
    }
}
