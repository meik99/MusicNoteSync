package at.htl_leonding.musicnotesync.bluetooth.socket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import at.htl_leonding.musicnotesync.blt.BltConstants;

/**
 * Created by michael on 1/8/17.
 */

public class Server implements Runnable, SocketWatcher.SocketWatcherListener {
    public static final String GET = "GET";

    public interface ServerListener {

        void onServerDeviceConnected(BluetoothSocket socket);

        void onServerMessageReceived(BluetoothSocket socket, String message);

        void onServerDeviceDisconnected(BluetoothSocket socket);
    }

    private static Server instance;

    private List<ServerListener> mServerListener;
    private List<BluetoothSocket> mClients;
    private Executor mExecutor;
    private boolean mRunning = false;

    public static Server getInstance(){
        if(instance == null){
            instance = new Server();
        }
        return instance;
    }

    public void startServer(){
        if(mRunning == false){
            mExecutor.execute(instance);
            mRunning = true;
        }
    }

    public void stopServer(){
        mRunning = false;
    }

    private Server() {
        this.mServerListener = new LinkedList<>();
        mClients = new LinkedList<>();
        mExecutor = BluetoothExecutor.BLUETOOTH_EXECUTOR;
    }

    @Override
    public void run() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            try {
                BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                        bluetoothAdapter.getName(),
                        BltConstants.CONNECTION_UUID
                );

                while (mRunning == true) {
                    BluetoothSocket clientSocket = serverSocket.accept();

                    if(clientSocket != null) {
                        mClients.add(clientSocket);
                        SocketWatcher watcher = new SocketWatcher(clientSocket);
                        watcher.addListener(this);
                        mExecutor.execute(watcher);

                        notifyOnConnected(clientSocket);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        mRunning = false;
    }

    @Override
    public void onMessageReceived(BluetoothSocket socket, String message) {
        for(ServerListener listener: mServerListener){
            listener.onServerMessageReceived(socket, message);
        }
    }

    @Override
    public void onDisconnected(BluetoothSocket socket) {
        for(ServerListener listener: mServerListener){
            listener.onServerDeviceDisconnected(socket);
        }
        this.mClients.remove(socket);
    }



    private void notifyOnConnected(BluetoothSocket socket){
        for(ServerListener listener : mServerListener){
            listener.onServerDeviceConnected(socket);
        }
    }

    public void addListener(ServerListener listener){
        if(mServerListener.contains(listener) == false){
            mServerListener.add(listener);
        }
    }

    public void removeListener(ServerListener listener){
        mServerListener.remove(listener);
    }
}
