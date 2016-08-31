package at.htl_leonding.musicnotesync.bluetooth.client;

import android.bluetooth.BluetoothDevice;
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
public class BluetoothClientController {
    private static final String TAG = BluetoothClientController.class.getSimpleName();

    private final BluetoothClientModel mModel;

    protected BluetoothClientController(BluetoothDevice server){
        this.mModel = new BluetoothClientModel();
        BluetoothSocket socket = null;

        try {

            socket = server.createRfcommSocketToServiceRecord(BluetoothConstants.CONNECTION_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mModel.setSocket(socket);
    }

    public boolean initiateHandshake() {
        mModel.setHandshakeSucceeded(false);

        if(mModel.getSocket().isConnected() == false){
            try {
                mModel.getSocket().connect();
            } catch (IOException e) {
                Log.i(TAG, "initiateHandshake: " + e.getMessage());
                return false;
            }
        }
        if(mModel.getSocket().isConnected() == false) {
            return false;
        }

        OutputStream os = mModel.getOutputStream();
        InputStream is = mModel.getInputStream();

        if(os == null) return false;
        if(is == null) return false;

        byte[] buffer = ByteBuffer.allocate(4).putInt(RfcommProtocol.DTR.ordinal()).array();
        try {
            os.write(buffer);
        } catch (IOException e) {
            Log.i(TAG, "initiateHandshake: " + e.getMessage());
            return false;
        }

        buffer = new byte[4];

        try {
            int answer;

            is.read(buffer);

            answer = ByteBuffer.wrap(buffer).getInt();

            if(answer != RfcommProtocol.DSR.ordinal()) return false;

            is.read(buffer);
            answer = ByteBuffer.wrap(buffer).getInt();

            if(answer != RfcommProtocol.DCD.ordinal()) return false;

        } catch (IOException e) {
            Log.i(TAG, "initiateHandshake: " + e.getMessage());
            return false;
        }

        mModel.setHandshakeSucceeded(true);
        return true;
    }

    public void closeConnection() throws IOException {
        mModel.getSocket().close();
    }
}
