package at.htl_leonding.musicnotesync.bluetooth.server;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.bluetooth.communication.Flag;
import at.htl_leonding.musicnotesync.db.NotesheetContract;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.global.Constant;

/**
 * Created by michael on 05.09.16.
 */
public class ClientWrapper {
    private static final String TAG = ClientWrapper.class.getSimpleName();

    private BluetoothSocket mSocket;
    private boolean mHandshakeSuccessful;

    public ClientWrapper(BluetoothSocket socket){
        if(socket == null) {
            throw new IllegalArgumentException();
        }

        mSocket = socket;
        mHandshakeSuccessful = false;
    }

    /**
     * Performs the handshake with the client.
     * Client has to initiate it first.
     * @return
     * @throws IOException
     */
    public boolean performHandshake() throws IOException {
        OutputStream os;
        InputStream is;
        byte[] buffer = new byte[4];
        Flag answer;

        if(mSocket.isConnected() == false){
            mSocket.connect();
        }

        os = mSocket.getOutputStream();
        is = mSocket.getInputStream();

        if(os == null || is == null) return false;

        is.read(buffer);
        answer = Flag.fromByteArray(buffer);

        if(answer != Flag.CONNECT) return false;

        buffer = Flag.toByteArray(Flag.ACK);
        os.write(buffer);

        return true;
    }

    public boolean sendNotesheet(Notesheet notesheet) throws IOException {
        OutputStream os;
        InputStream is;
        byte[] buffer;
        Flag answer;

        if(mSocket.isConnected() == false){
            mSocket.connect();
        }

        os = mSocket.getOutputStream();
        is = mSocket.getInputStream();

        buffer = Flag.toByteArray(Flag.CONNECT);
        os.write(buffer);
        is.read(buffer);

        answer = Flag.fromByteArray(buffer);
        if(answer != Flag.ACK) return false;

        buffer = Flag.toByteArray(Flag.FILE);
        os.write(buffer);
        is.read(buffer);
        answer = Flag.fromByteArray(buffer);
        if(answer != Flag.ACK) return false;

        JSONObject jObj = new JSONObject();
        try {
            jObj.put(NotesheetContract.NotesheetEntry.COLUMN_UUID, notesheet.getUUID());
            jObj.put(NotesheetContract.NotesheetEntry.COLUMN_FILE_NAME, notesheet.getName());
        } catch (JSONException e) {
            Log.i(TAG, "sendNotesheet: " + e.getMessage());
            return false;
        }

        byte[] flag = Flag.toByteArray(Flag.META);
        byte[] jObjectArray = jObj.toString().getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(jObjectArray);

        buffer = new byte[BluetoothConstants.BUFFER_SIZE];

        while(bais.read(buffer, 4, buffer.length-4) > -1){
            buffer = copyByteArrayToBuffer(flag, buffer, 0);
            os.write(buffer);

        }

        buffer = Flag.toByteArray(Flag.EOT);
        os.write(buffer);

        buffer = new byte[4];
        is.read(buffer);
        answer = Flag.fromByteArray(buffer);

        if(answer == Flag.ACK){
            BufferedInputStream bis =
                    new BufferedInputStream(new FileInputStream(notesheet.getFile()));
            byte[] fileBuffer = new byte[1024];
            buffer = new byte[1028];

            while(bis.read(fileBuffer) > -1){
                copyByteArrayToBuffer(Flag.toByteArray(Flag.DATA), buffer, 0);
                copyByteArrayToBuffer(fileBuffer, buffer, 4);
                os.write(buffer);
            }

            buffer = Flag.toByteArray(Flag.EOT);
            os.write(buffer);
            is.read(buffer);

            if(Flag.fromByteArray(buffer) != Flag.ACK) return false;
        }

        return true;
    }

    public boolean isHandshakeSuccessful() {
        return mHandshakeSuccessful;
    }

    private byte[] copyByteArrayToBuffer(byte[] source, byte[] destination, int offset){
        for(int i = 0; i < source.length; i++){
            destination[i+offset] = source[i];
        }

        return destination;
    }
}
