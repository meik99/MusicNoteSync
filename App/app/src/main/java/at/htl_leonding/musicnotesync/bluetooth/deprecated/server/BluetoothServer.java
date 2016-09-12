package at.htl_leonding.musicnotesync.bluetooth.deprecated.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothServer extends Thread{
    private static final String TAG = BluetoothServer.class.getSimpleName();

    private final BluetoothServerController serverController;

    private boolean mRunning;


    public BluetoothServer(BluetoothAdapter bluetoothAdapter){
        mRunning = true;
        serverController = new BluetoothServerController(bluetoothAdapter);
    }

    @Override
    public void run() {
        BluetoothSocket socket;
        mRunning = true;

        while(mRunning == true){
            socket = serverController.waitForClient();

            if(socket != null){
                boolean clientAdded = serverController.addClient(socket);

                if(clientAdded == true){
                    Log.i(TAG, "run: Client was added");
                }else{
                    Log.i(TAG, "run: Adding client resulted in failure");
                }
            }
        }
    }

    public void cancel(){
        mRunning = false;
    }
}
