package at.htl_leonding.musicnotesync.bluetooth.deprecated.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 30.08.16.
 */
public class BluetoothServerModel {
    private static final String TAG = BluetoothServerModel.class.getSimpleName();

    private final List<ClientWrapper> clients;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket socket;

    public BluetoothServerModel() {
        clients = new LinkedList<>();
    }

    public List<ClientWrapper> getClients() {
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
}
