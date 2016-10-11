package at.htl_leonding.musicnotesync.bluetooth.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Pack200;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 19.09.16.
 */

public class Server extends Thread{
    private static final String TAG = Server.class.getSimpleName();
    private static Server instance;

    private BluetoothServerSocket serverSocket;
    private boolean running;
    private List<BluetoothSocket> clients;
    private PackageSender sender;

    public Server(){
        clients = new LinkedList<>();
        sender = PackageSender.getInstance();
        running = false;
    }

    @Override
    public void run() {
        super.run();
        running = true;

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if(adapter != null && adapter.isEnabled()) {
                serverSocket =
                        adapter.listenUsingRfcommWithServiceRecord(
                                adapter.getName(), BluetoothConstants.CONNECTION_UUID);

                while (running == true) {
                    BluetoothSocket socket = serverSocket.accept();

                    clients.add(socket);
                }
            }
        } catch (IOException e) {
            Log.i(TAG, "run: " +e.getMessage());
            running = false;
        }

        running = false;
    }

    public void stopServer(){
        running = false;
        if(serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "stopServer: " + e.getMessage());
            }
            finally {
                serverSocket = null;
            }
        }
    }

    public void startServer(){
        if(running == false){
            this.start();
        }
    }

    public void sendPackage(BluetoothPackage message){
        sender.addMessage(message);
    }

    public static Server getInstance(){
        if(instance == null){
            instance = new Server();
        }
        return instance;
    }

    public boolean isConnected() {
        return clients.size() > 0;
    }

    public boolean isRunning() {
        return running;
    }
}
