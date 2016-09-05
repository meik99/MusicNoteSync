package at.htl_leonding.musicnotesync.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.bluetooth.communication.BluetoothCommunicator;
import at.htl_leonding.musicnotesync.bluetooth.communication.Flag;

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

        BluetoothCommunicator.init(null, null, this);
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

        byte[] buffer = ByteBuffer.allocate(4).putInt(Flag.CONNECT.ordinal()).array();
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

            if(answer != Flag.ACK.ordinal()) return false;

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

    public Flag awaitFlag() throws IOException {
        Flag result;
        byte[] buffer = new byte[4];
        InputStream is = mModel.getInputStream();

        if(is == null) {
            Log.i(TAG, "awaitFlag: Input stream is null");
            return null;
        }

        is.read(buffer);

        result = Flag.fromByteArray(buffer);
        return result;
    }

    public void sendAcknowledge() throws IOException {
        byte[] buffer = Flag.toByteArray(Flag.ACK);
        OutputStream os = mModel.getOutputStream();

        if(os == null){
            Log.i(TAG, "sendAcknowledge: Output stream is null");
        }
        else{
            os.write(buffer);
        }
    }

    public void receiveNotesheet() throws IOException {
        sendAcknowledge();

        byte[] buffer = new byte[1028];
        OutputStream os = mModel.getOutputStream();
        InputStream is = mModel.getInputStream();
        String tmp = new String();
        int read;
        if(os == null || is == null) throw new IOException
                ("Output stream or input stream is null");

        while((read = is.read(buffer)) > -1){
            if(read > 4){

            }
        }
    }
}
