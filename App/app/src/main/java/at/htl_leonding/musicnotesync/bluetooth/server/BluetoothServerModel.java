package at.htl_leonding.musicnotesync.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 30.08.16.
 */
public class BluetoothServerModel {
    private static final String TAG = BluetoothServerModel.class.getSimpleName();

    private final List<BluetoothSocket> clients;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket socket;
    private boolean handshakeSucceeded = false;

    public BluetoothServerModel() {
        clients = new LinkedList<>();
    }

    public OutputStream getOutputStream(int index){
        if(index >= 0 && index < clients.size()){
            if(clients.get(index) != null){
                try {
                    return clients.get(index).getOutputStream();
                } catch (IOException e) {
                    Log.i(TAG, "getOutputStream: " + e.getMessage());
                    return null;
                }
            }else return null;
        }else return null;
    }

    public InputStream getInputStream(int index){
        if(index >= 0 && index < clients.size()){
            if(clients.get(index) != null){
                try {
                    return clients.get(index).getInputStream();
                } catch (IOException e) {
                    Log.i(TAG, "getInputStream: " + e.getMessage());
                    return null;
                }
            }else return null;
        }else return null;
    }

    public List<BluetoothSocket> getClients() {
        return clients;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public BluetoothServerSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothServerSocket socket) {
        this.socket = socket;
    }

    public boolean isHandshakeSucceeded() {
        return handshakeSucceeded;
    }

    public void setHandshakeSucceeded(boolean handshakeSucceeded) {
        this.handshakeSucceeded = handshakeSucceeded;
    }
}
