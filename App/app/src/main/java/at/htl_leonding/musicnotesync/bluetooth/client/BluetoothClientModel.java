package at.htl_leonding.musicnotesync.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by michael on 30.08.16.
 */
public class BluetoothClientModel {
    private BluetoothSocket socket;
    private BluetoothDevice server;
    private boolean handshakeSucceeded;

    protected BluetoothClientModel(){

    }

    public InputStream getInputStream() {
        try {
            if(socket != null) {
                return socket.getInputStream();
            }else{
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public OutputStream getOutputStream() {
        try {
            if(socket != null) {
                return socket.getOutputStream();
            }else{
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public BluetoothDevice getServer() {
        return server;
    }

    public void setServer(BluetoothDevice server) {
        this.server = server;
    }

    public boolean isHandshakeSucceeded() {
        return handshakeSucceeded;
    }

    public void setHandshakeSucceeded(boolean handshakeSucceeded) {
        this.handshakeSucceeded = handshakeSucceeded;
    }
}
