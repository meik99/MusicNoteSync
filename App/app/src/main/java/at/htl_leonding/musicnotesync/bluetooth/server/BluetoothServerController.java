package at.htl_leonding.musicnotesync.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.bluetooth.communication.BluetoothCommunicator;
import at.htl_leonding.musicnotesync.bluetooth.communication.Flag;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.global.Constant;

/**
 * Created by michael on 30.08.16.
 */
public class BluetoothServerController {
    private static final String TAG = BluetoothServerController.class.getSimpleName();

    private final BluetoothServerModel mModel;
    private Exception sendException = null;

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

        BluetoothCommunicator.init(null, this, null);
    }

    public BluetoothSocket waitForClient(){
        BluetoothSocket newClient = null;
        try {
            if(mModel != null && mModel.getSocket() != null) {
                newClient = mModel.getSocket().accept();
            }
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


    public boolean addClient(BluetoothSocket socket) {
        ClientWrapper client = new ClientWrapper(socket);
        try {
            if(client.performHandshake() == true){
                mModel.getClients().add(client);
                return true;
            }

            return false;
        } catch (IOException e) {
            Log.i(TAG, "performHandshake: " + e.getMessage());
            return false;
        }
    }


/*    public boolean sendNotesheetToClients(Notesheet ns) {
        boolean result = true;
        List<BluetoothSocket> clients = mModel.getClients();

        for(int i = 0; i < clients.size(); i++){
            boolean tmpResult = true;
            int tryCount = 0;

            do {
                if(clientHasFile(i, ns) == false){
                    tmpResult = sendMetadataToClient(i, ns);
                }else{
                    //Client has file
                    //Open file on Client
                }
            }while(tmpResult == false && tryCount++ < 10);
        }

        return false;
    }

    private boolean sendMetadataToClient(int i, Notesheet ns) {
        BluetoothSocket client = mModel.getClients().get(i);
        OutputStream os = mModel.getOutputStream(i);
        InputStream is = mModel.getInputStream(i);
        byte[] buffer;

        if (client == null ||
                os == null ||
                is == null) {
            sendException = new IllegalArgumentException();
            return false;
        }

        buffer = new byte[4];
        try {
            //Check if client is ready to recieve file
            int response;
            int tryCount = 0;

            is.read(buffer);
            response = ByteBuffer.wrap(buffer).getInt();

            if(response != Flag.RTS.ordinal()) return false;

            buffer = Flag.toByteArray(Flag.CTS);
            os.write(buffer);

            response = getResponse(is);

            if(response != Flag.ACK.ordinal()) return false;

            //Send file metadata
            buffer = Flag.toByteArray(Flag.SOH);
            os.write(buffer);

            response = getResponse(is);

            if(response != Flag.ACK.ordinal()) return false;

            StringBuilder message = new StringBuilder();
            message.append("UUID:");
            message.append(ns.getUUID());
            message.append(";");
            message.append("NAME:");
            message.append(ns.getName());

            do {
                buffer = message.toString().getBytes();
                os.write(buffer);
                response = getResponse(is);
            }while(response != Flag.ACK.ordinal() && tryCount++ < Constant.TRY_MAX);

            if(tryCount >= Constant.TRY_MAX) throw new IOException("Couldn't send metadata");

            return true;

        } catch (IOException e) {
            sendException = e;
            Log.i(TAG, "sendMetadataToClient: " + e.getMessage());
            return false;
        }
    }

    private int getResponse(InputStream is) throws IOException {
        byte[] buffer = new byte[4];
        int response;

        is.read(buffer);
        response = byteArrayToInt(buffer);
        return  response;
    }

    private int byteArrayToInt(byte[] buffer){
        return ByteBuffer.wrap(buffer).getInt();
    }

    private boolean clientHasFile(int i, Notesheet ns){
        BluetoothSocket client = mModel.getClients().get(i);
        OutputStream os = mModel.getOutputStream(i);
        InputStream is = mModel.getInputStream(i);
        byte[] buffer;

        if (client == null ||
                os == null ||
                is == null) {
            sendException = new IllegalArgumentException();
            return false;
        }

        buffer = ByteBuffer.allocate(4).putInt(Flag.ENQ.ordinal()).array();

        try {
            int response;

            //Check if client has file
            os.write(buffer);
            buffer = ns.getUUID().getBytes();
            os.write(buffer);

            //Await response
            buffer = new byte[4];
            is.read(buffer);
            response = ByteBuffer.wrap(buffer).getInt();
            if(response == Flag.ACK.ordinal()){
                return true;
            }else if(response == Flag.NAK.ordinal()){
                return false;
            }else{
                throw new IOException("Unexpected answer from client");
            }

        } catch (IOException e) {
            sendException = e;
            return false;
        }
    }

    private Flag sendCommandForResponse(
            int i,
            Flag command,
            Flag expectedResponse){
        BluetoothSocket client = mModel.getClients().get(i);
        OutputStream os = mModel.getOutputStream(i);
        InputStream is = mModel.getInputStream(i);

        if (client == null ||
                os == null ||
                is == null) {
            sendException = new IllegalArgumentException();
            return null;
        }

        byte[] buffer;
        buffer = ByteBuffer.allocate(4).putInt(command.ordinal()).array();

        try {
            int response;

            os.write(buffer);
            is.read(buffer);
            response = ByteBuffer.wrap(buffer).getInt();

            if(response == expectedResponse.ordinal()){
                return expectedResponse;
            }
            else{
                for(Flag com : Flag.values()){
                    if(com.ordinal() == response){
                        return com;
                    }
                }

                throw new IOException("Unexpected response from client");
            }
        } catch (IOException e) {
            Log.i(TAG, "sendCommand: " + e.getMessage());
            sendException = e;
            return null;
        }
    }*/
}
