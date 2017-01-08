//package at.htl_leonding.musicnotesync.bluetooth.connection.server;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.util.Base64;
//import android.util.Base64InputStream;
//import android.util.Log;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//
//import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
//import at.htl_leonding.musicnotesync.bluetooth.connection.Connection;
//import at.htl_leonding.musicnotesync.bluetooth.connection.ConnectionManager;
//
///**
// * Created by michael on 19.09.16.
// */
//
//public class Server implements Runnable, Connection.ConnectionListener {
//    @Override
//    public void onClientMessageReceived(Connection connection, String message) {
//        notifyOnReceived(connection, message);
//    }
//
//    @Override
//    public void onConnectionClosed(Connection connection) {
//
//    }
//
//    protected interface ServerListener {
//        void onConnect(Connection connection);
//        void onMessage(Connection connection, String message);
//    }
//
//    private static final String TAG = Server.class.getSimpleName();
//    private static Server instance;
//
//    private BluetoothServerSocket mServerSocket;
//    private boolean mRunning;
//    private List<ServerListener> mListener;
//    private Executor mExecuter;
//
//    protected Server() {
//        mRunning = false;
//        mListener = new ArrayList<>();
//        mExecuter = new ScheduledThreadPoolExecutor(
//                Runtime.getRuntime().availableProcessors()
//        );
//    }
//
//    @Override
//    public void run() {
//        mRunning = true;
//
//        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        try {
//            if (adapter != null && adapter.isEnabled()) {
//                mServerSocket =
//                        adapter.listenUsingRfcommWithServiceRecord(
//                                adapter.getName(), BluetoothConstants.CONNECTION_UUID);
//
//                try{
//                    while (mRunning == true) {
//                        BluetoothSocket socket = mServerSocket.accept();
//
//                        if(socket != null) {
//                            Connection connection = new Connection(socket);
//
//                            notifyOnConnect(connection);
//                            connection.addListener(this);
//                        }
//                    }
//                }catch(Exception e){
//                    Log.i(TAG, "run: " + e.getClass().getSimpleName());
//                    Log.i(TAG, "run: " + e.getMessage());
//                }
//
//            }
//        } catch (IOException e) {
//            Log.i(TAG, "run: " + e.getMessage());
//            mRunning = false;
//        }
//
//        mRunning = false;
//    }
//
////    private void startWatcherThread(final BluetoothSocket socket) {
////        Thread watchSocketThread = new Thread(new Runnable() {
////            @Override
////            public void run() {
////                try {
////                    InputStream socketInputStream = socket.getInputStream();
////                    BluetoothSocket bluetoothSocket = socket;
////                    if (socketInputStream != null) {
////                        Base64InputStream base64InputStream =
////                                new Base64InputStream
////                                        (socketInputStream, Base64.DEFAULT);
////
////                        while(true) {
////                            byte[] buffer =
////                                    new byte[BluetoothConstants.BUFFER_MAX_SIZE];
////                            int received = -1;
////
////                            received = base64InputStream.read(buffer);
////
////                            if (received > -1) {
////                                notifyOnReceived(
////                                        bluetoothSocket,
////                                        new String(buffer));
////                            }
////                        }
////                    }
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
////        });
////        mExecuter.execute(watchSocketThread);
////    }
//
//    private void notifyOnReceived(Connection connection, String message) {
//        for (ServerListener listener : mListener){
//            listener.onMessage(connection, message);
//        }
//    }
//
//    public void notifyOnConnect(Connection connection){
//        for (ServerListener listener : mListener){
//            listener.onConnect(connection);
//        }
//    }
//
//    public void addListener(ServerListener listener){
//        if(listener != null) {
//            mListener.add(listener);
//        }
//    }
//
//    public void removeListener(ServerListener listener){
//        if(listener != null){
//            mListener.remove(listener);
//        }
//    }
//
////    public void stopServer(){
////        mRunning = false;
////        if(mServerSocket != null){
////            try {
////                mServerSocket.close();
////            } catch (IOException e) {
////                Log.i(TAG, "stopServer: " + e.getMessage());
////            }
////            finally {
////                mServerSocket = null;
////            }
////        }
////    }
////
////    public void sendPackage(BluetoothPackage message) {
////        PackageSender.getInstance().addMessage(message);
////    }
////
////    public static Server getInstance(){
////        if(instance == null){
////            instance = new Server();
////        }
////        return instance;
////    }
//
////    public boolean isConnected() {
////        return clients.size() > 0;
////    }
////
////    public boolean isRunning() {
////        return mRunning;
////    }
//
////    public void sendFile(Notesheet ns) {
////        BluetoothPackage file = new BluetoothPackage();
////        ByteBuffer bb = ByteBuffer.allocate(BluetoothConstants.BUFFER_CONTENT_SIZE);
////        File noteFile = ns.getFile();
////
////        file.setFlag(Flag.FILE);
////        bb.put(ns.getUUID().getBytes());
////        bb.put(";".getBytes());
////        bb.put(ns.getPath().getBytes());
////        file.setContent(bb.array());
////
////        sendPackage(file);
////
////        for(BluetoothSocket client : this.clients){
////            if(noteFile.exists()){
////                try {
////                    BufferedInputStream is = new BufferedInputStream(
////                            new FileInputStream(noteFile));
////                    BufferedOutputStream os = new BufferedOutputStream(
////                            client.getOutputStream());
////
////                    byte[] buffer = new byte[BluetoothConstants.BUFFER_FILE_BUFFER];
////                    int len = is.read(buffer);
////
////                    if(len > -1){
////                        os.write(buffer);
////                    }
////
////                    while((len = is.read(buffer)) > -1){
////                        os.write(buffer);
////                    }
////
////                } catch (FileNotFoundException e) {
////                    try {
////                        client.getOutputStream().write(new byte[]{-1});
////                    } catch (IOException e1) {
////                        Log.d(TAG, "sendFile: " + e1.getMessage());
////                    }
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
////        }
////      }
//}
