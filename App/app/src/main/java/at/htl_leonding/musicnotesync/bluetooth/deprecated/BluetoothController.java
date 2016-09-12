package at.htl_leonding.musicnotesync.bluetooth.deprecated;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothActivity;
import at.htl_leonding.musicnotesync.bluetooth.deprecated.client.BluetoothClient;
import at.htl_leonding.musicnotesync.bluetooth.deprecated.communication.BluetoothCommunicator;
import at.htl_leonding.musicnotesync.bluetooth.deprecated.server.BluetoothServer;
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
    private boolean mIsServer = true;

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
        BluetoothCommunicator.init(this, null, null);
    }

    public void getPermissions(){
        mPermissionsGranted = PermissionHelper.verifyPermissions(mActivity, new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
    }
    public boolean discoverDevices(){
        if(mPermissionsGranted == true && this.mBluetoothAdapter.isDiscovering() == false) {
            if(mBluetoothAdapter.isEnabled() == false){
                return false;
            }

            Log.i(TAG, "discoverDevices: " + "starting discovery");

            IntentFilter bltFoundDeviceFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            try{
                mActivity.unregisterReceiver(this.mBluetoothDeviceReciever);
            }catch(IllegalArgumentException e){
                Log.i(TAG, "discoverDevices: receiver not yet registered");
            }
            try {
                mActivity.registerReceiver(this.mBluetoothDeviceReciever, bltFoundDeviceFilter);
            }catch(IllegalArgumentException e){
                Log.i(TAG, "discoverDevices: receiver already registered");
            }

            Log.i(TAG, "discoverDevices: " + "register receiver");

            this.mDeviceBuffer.clear();
            this.mBluetoothAdapter.startDiscovery();

            return true;
        }

        return false;
    }

    public boolean isBluetoothEnabled(){
        return mBluetoothAdapter.isEnabled();
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
            this.mIsServer = true;
        }
    }

    public void stopServer() {
        if(this.mServer != null && this.mServer.isAlive()){
            this.mServer.cancel();
            this.mServer = null;
            this.mIsServer = false;
        }
    }

    public boolean isServer() {
        return mClients.size() > 0;
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

    public boolean hasPermissions(){
        return this.mPermissionsGranted;
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

    public boolean isDiscovering() {
        return mBluetoothAdapter.isDiscovering();
    }

    public boolean hasServerStarted() {
        return mServer != null && mServer.isAlive();
    }

    public boolean isDiscoverable() {
        return mBluetoothAdapter.getScanMode() ==
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }
}
