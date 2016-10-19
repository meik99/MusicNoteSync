package at.htl_leonding.musicnotesync.bluetooth.connection.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 19.09.16.
 */

public class Server implements Runnable {
    protected interface ServerListener {
        void onConnect(BluetoothSocket socket);
    }

    private static final String TAG = Server.class.getSimpleName();
    private static Server instance;

    private BluetoothServerSocket mServerSocket;
    private boolean mRunning;
    private List<ServerListener> mListener;

    protected Server() {
        mRunning = false;
        mListener = new ArrayList<>();
    }

    @Override
    public void run() {
        mRunning = true;

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if (adapter != null && adapter.isEnabled()) {
                mServerSocket =
                        adapter.listenUsingRfcommWithServiceRecord(
                                adapter.getName(), BluetoothConstants.CONNECTION_UUID);

                try{
                    while (mRunning == true) {
                        BluetoothSocket socket = mServerSocket.accept();
                        notifiyListener(socket);
                    }
                }catch(Exception e){
                    Log.i(TAG, "run: " + e.getClass().getSimpleName());
                    Log.i(TAG, "run: " + e.getMessage());
                }

            }
        } catch (IOException e) {
            Log.i(TAG, "run: " + e.getMessage());
            mRunning = false;
        }

        mRunning = false;
    }

    public void notifiyListener(BluetoothSocket socket){
        for (ServerListener listener : mListener){
            listener.onConnect(socket);
        }
    }

    public void addListener(ServerListener listener){
        if(listener != null) {
            mListener.add(listener);
        }
    }

    public void removeListener(ServerListener listener){
        if(listener != null){
            mListener.remove(listener);
        }
    }

//    public void stopServer(){
//        mRunning = false;
//        if(mServerSocket != null){
//            try {
//                mServerSocket.close();
//            } catch (IOException e) {
//                Log.i(TAG, "stopServer: " + e.getMessage());
//            }
//            finally {
//                mServerSocket = null;
//            }
//        }
//    }
//
//    public void sendPackage(BluetoothPackage message) {
//        PackageSender.getInstance().addMessage(message);
//    }
//
//    public static Server getInstance(){
//        if(instance == null){
//            instance = new Server();
//        }
//        return instance;
//    }

//    public boolean isConnected() {
//        return clients.size() > 0;
//    }
//
//    public boolean isRunning() {
//        return mRunning;
//    }

//    public void sendFile(Notesheet ns) {
//        BluetoothPackage file = new BluetoothPackage();
//        ByteBuffer bb = ByteBuffer.allocate(BluetoothConstants.BUFFER_CONTENT_SIZE);
//        File noteFile = ns.getFile();
//
//        file.setFlag(Flag.FILE);
//        bb.put(ns.getUUID().getBytes());
//        bb.put(";".getBytes());
//        bb.put(ns.getPath().getBytes());
//        file.setContent(bb.array());
//
//        sendPackage(file);
//
//        for(BluetoothSocket client : this.clients){
//            if(noteFile.exists()){
//                try {
//                    BufferedInputStream is = new BufferedInputStream(
//                            new FileInputStream(noteFile));
//                    BufferedOutputStream os = new BufferedOutputStream(
//                            client.getOutputStream());
//
//                    byte[] buffer = new byte[BluetoothConstants.BUFFER_FILE_BUFFER];
//                    int len = is.read(buffer);
//
//                    if(len > -1){
//                        os.write(buffer);
//                    }
//
//                    while((len = is.read(buffer)) > -1){
//                        os.write(buffer);
//                    }
//
//                } catch (FileNotFoundException e) {
//                    try {
//                        client.getOutputStream().write(new byte[]{-1});
//                    } catch (IOException e1) {
//                        Log.d(TAG, "sendFile: " + e1.getMessage());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//      }
}
