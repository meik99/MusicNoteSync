package at.htl_leonding.musicnotesync.blt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Set;

import at.htl_leonding.musicnotesync.blt.receiver.BluetoothState;
import at.htl_leonding.musicnotesync.blt.receiver.DeviceFoundReceiver;
import at.htl_leonding.musicnotesync.blt.receiver.StateChangedReceiver;

/**
 * Created by michael on 3/11/17.
 */

public class BltService extends Service {
    public static final String TAG = BltService.class.getSimpleName();

    private BluetoothState mBluetoothState;
    private BltServerThread mBltServer;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BltService() {
        super();
    }

    private void startDiscovery(){
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    private void stopDiscovery(){
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
    }

    private void waitForConnection() {
       if(mBltServer == null || mBltServer.isAlive() == false){
           mBltServer = new BltServerThread(this);
           mBltServer.start();
       }
    }


    private void queryPairedDevices(){
        Set<BluetoothDevice> pairedDevices =
                BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        for (BluetoothDevice device :
                pairedDevices) {
            deviceFound(device);
        }
    }

    public void deviceFound(BluetoothDevice device) {
        BltRepository.getInstance().addFoundDevice(device);
    }

    public void setBluetoothState(BluetoothState bluetoothState) {
        this.mBluetoothState = bluetoothState;

        startSearchAndServer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter deviceFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter stateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(new DeviceFoundReceiver(this),deviceFoundFilter);
        registerReceiver(new StateChangedReceiver(this), stateChangedFilter);


        startSearchAndServer();
    }

    private void startSearchAndServer(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter != null){

            if(adapter.isEnabled()){
                mBluetoothState = BluetoothState.ON;
                startDiscovery();
                waitForConnection();
            }else{
                mBluetoothState = BluetoothState.OFF;
                adapter.cancelDiscovery();

                if(mBltServer != null) {
                    mBltServer.stopServer();
                }
            }

        }
    }


    public BluetoothState getBluetoothState() {
        return mBluetoothState;
    }
}
