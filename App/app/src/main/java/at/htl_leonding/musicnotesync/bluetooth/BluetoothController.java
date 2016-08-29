package at.htl_leonding.musicnotesync.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.client.BluetoothClient;
import at.htl_leonding.musicnotesync.bluetooth.server.BluetoothServer;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothController{
    public static final int ENABLE_BLT_REQUEST = 6;

    private static final String TAG = BluetoothController.class.getSimpleName();

    private List<BluetoothDevice> mDeviceBuffer = new LinkedList<>();
    private List<BluetoothDeviceFoundListener> mDeviceFoundListeners = new LinkedList<>();
    private List<BluetoothClient> mClients = new LinkedList<>();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothActivity mActivity;
    private BluetoothDeviceReciever mBluetoothDeviceReciever;
    private BluetoothServer mServer;

    private boolean mPermissionsGranted = false;

    public BluetoothController(BluetoothActivity activity){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if(adapter == null){
            //TODO:
            //Device doesn't support bluetooth
            //Handle with error message
        }
        this.mBluetoothAdapter = adapter;
        this.mActivity = activity;
        this.mBluetoothDeviceReciever = new BluetoothDeviceReciever(this);
    }

    public void getPermissions(){
        mPermissionsGranted = PermissionHelper.verifyPermissions(mActivity, new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
    }
    public void discoverDevices(){
        if(mPermissionsGranted == true && this.mBluetoothAdapter.isDiscovering() == false) {
            if(mBluetoothAdapter.isEnabled() == false){
                enableBluetooth();
            }

            Log.i(TAG, "discoverDevices: " + "starting discovery");

            IntentFilter bltFoundDeviceFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            try {
                mActivity.registerReceiver(this.mBluetoothDeviceReciever, bltFoundDeviceFilter);
            }catch(IllegalArgumentException e){
                Log.i(TAG, "discoverDevices: receiver already registered");
            }

            Log.i(TAG, "discoverDevices: " + "register receiver");

            this.mDeviceBuffer.clear();
            this.mBluetoothAdapter.startDiscovery();
        }
    }

    public void cancelDiscovery(){
        if(mPermissionsGranted == true) {
            Log.i(TAG, "cancelDiscovery: Cancel discovery and unregister receiver");
            this.mBluetoothAdapter.cancelDiscovery();
        }
    }

    public void enableBluetooth(){
        if(mPermissionsGranted == true) {
            if (this.mBluetoothAdapter.isEnabled() == false) {
                Intent enableBlt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBlt, ENABLE_BLT_REQUEST);
            }
        }
    }

    public void startServer(){
        if(this.mServer == null) {
            this.mServer = new BluetoothServer(this.mBluetoothAdapter);
            this.mServer.start();
        }
    }

    public void stopServer() {
        if(this.mServer != null && this.mServer.isAlive()){
            this.mServer.cancel();
            this.mServer = null;
        }
    }

    public void addDevice(BluetoothDevice device) {
        if(device != null){
            Log.i(TAG, "addDevice: Device found " + device.getName());
            if(this.mDeviceBuffer.contains(device) == false &&
                    this.deviceInBuffer(device) == false) {
                this.mDeviceBuffer.add(device);
            }

            for(BluetoothDeviceFoundListener listener : mDeviceFoundListeners){
                listener.deviceFound(device);
            }
        }
    }

    private boolean deviceInBuffer(BluetoothDevice device){
        if(this.mDeviceBuffer == null || this.mDeviceBuffer.size() <= 0){
            return false;
        }
        if(device == null){
            return true;
        }

        for(BluetoothDevice tmpDevice : this.mDeviceBuffer){
            if(tmpDevice.getAddress().equals(device.getAddress())){
                return true;
            }
        }

        return false;
    }

    public void enableDiscoverability(){
        if(mPermissionsGranted == true){
            Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
            mActivity.startActivity(discoverIntent);
        }
    }

    public List<BluetoothDevice> getDevices(){
        return mDeviceBuffer != null ? mDeviceBuffer : new LinkedList<BluetoothDevice>();
    }

    public List<BluetoothClient> getClients() {
        return mClients;
    }

    public void setPermssionGranted(boolean permssionGranted) {
        this.mPermissionsGranted = permssionGranted;
    }

    public void registerDeviceFoundListener(BluetoothDeviceFoundListener listener){
        if(listener != null){
            this.mDeviceFoundListeners.add(listener);
        }
    }

    public void unregisterDeviceFoundListener(BluetoothDeviceFoundListener listener){
        if(listener != null){
            this.mDeviceFoundListeners.remove(listener);
        }
    }
}
