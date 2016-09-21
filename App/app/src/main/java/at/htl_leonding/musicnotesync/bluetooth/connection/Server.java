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
    private boolean running = true;
    private List<BluetoothSocket> clients;
    private PackageSender sender;

    public Server(){
        clients = new LinkedList<>();
        sender = new PackageSender();
    }

    @Override
    public void run() {
        running = true;
        super.run();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            serverSocket =
                    adapter.listenUsingRfcommWithServiceRecord(
                            adapter.getName(), BluetoothConstants.CONNECTION_UUID);

            while(running == true) {
                BluetoothSocket socket = serverSocket.accept();
                clients.add(socket);
            }
        } catch (IOException e) {
            Log.i(TAG, "run: " +e.getMessage());
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
            running = true;
            if(this.isAlive() == false) {
                this.start();
            }
        }
    }

    public void sendPackage(BluetoothPackage message){
        sender.addMessage(message);
        sender.send(clients);
    }

    public static Server getInstance(){
        if(instance == null){
            instance = new Server();
        }
        return instance;
    }
}
