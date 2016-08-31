package at.htl_leonding.musicnotesync.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.bluetooth.communication.RfcommProtocol;

/**
 * Created by michael on 30.08.16.
 */
public class BluetoothServerController {
    private static final String TAG = BluetoothServerController.class.getSimpleName();

    private final BluetoothServerModel mModel;

    public BluetoothServerController(BluetoothAdapter bluetoothAdapter) {
        BluetoothServerSocket serverSocket = null;
        mModel = new BluetoothServerModel();

        mModel.setBluetoothAdapter(bluetoothAdapter);

        try {
            serverSocket = mModel.getBluetoothAdapter().listenUsingRfcommWithServiceRecord(
                    mModel.getBluetoothAdapter().getName(), BluetoothConstants.CONNECTION_UUID );
        } catch (IOException e) {
            Log.i(TAG, "BluetoothServerController: " + e.getMessage());
        }

        mModel.setSocket(serverSocket);
    }

    public BluetoothSocket waitForClient(){
        BluetoothSocket newClient = null;
        try {
            newClient = mModel.getSocket().accept();
        } catch (IOException e) {
            Log.i(TAG, "waitForClient: " + e.getMessage());
        }

        return newClient;
    }

    public void cancel() {
        try {
            mModel.getSocket().close();
        } catch (IOException e) {
            Log.i(TAG, "cancel: " + e.getMessage());
        }
    }

    public boolean performHandshake(BluetoothSocket socket) {
        int index;
        byte[] buffer = new byte[4];

        mModel.setHandshakeSucceeded(false);
        mModel.getClients().add(socket);
        index = mModel.getClients().size()-1;

        InputStream is = mModel.getInputStream(index);
        OutputStream os = mModel.getOutputStream(index);

        if(is == null) return false;
        if(os == null) return false;

        try {
            is.read(buffer);
        } catch (IOException e) {
            Log.i(TAG, "performHandshake: " + e.getMessage());
            return false;
        }

        if(ByteBuffer.wrap(buffer).getInt() != RfcommProtocol.DTR.ordinal()) return false;

        buffer = ByteBuffer.allocate(4).putInt(RfcommProtocol.DSR.ordinal()).array();

        try {
            os.write(buffer);
        } catch (IOException e) {
            Log.i(TAG, "performHandshake: " + e.getMessage());
            return false;
        }

        buffer = ByteBuffer.allocate(4).putInt(RfcommProtocol.DCD.ordinal()).array();

        try {
            os.write(buffer);
        } catch (IOException e) {
            Log.i(TAG, "performHandshake: " + e.getMessage());
            return false;
        }

        mModel.setHandshakeSucceeded(true);
        return true;
    }
}
