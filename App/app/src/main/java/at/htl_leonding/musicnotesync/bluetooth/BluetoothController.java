package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.LinkedList;
import java.util.List;
import java.util.jar.Manifest;

import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothController{
    public static final int ENABLE_BLT_REQUEST = 6;

    private List<BluetoothDevice> deviceBuffer = new LinkedList<>();
    private List<BluetoothDeviceFoundListener> deviceFoundListeners = new LinkedList<>();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothActivity activity;
    private BluetoothDeviceReciever bluetoothDeviceReciever;
    private boolean permissionsGranted = false;

    public BluetoothController(BluetoothActivity activity){
        //Ask for bluetooth permissions

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null){
            //Device doesn't support bluetooth
            //Handle with error message
        }
        this.bluetoothAdapter = adapter;
        this.activity = activity;
        this.bluetoothDeviceReciever = new BluetoothDeviceReciever(this);
    }

    public void getPermissions(){
        PermissionHelper.verifyPermissions(activity, new String[]{
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN
        });
    }

    public void discoverDevices(){
        if(permissionsGranted) {
            IntentFilter bltFoundDeviceFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(this.bluetoothDeviceReciever, bltFoundDeviceFilter);

            this.deviceBuffer.clear();
            this.bluetoothAdapter.startDiscovery();
        }
    }

    public void cancelDiscovery(){
        if(permissionsGranted) {
            this.bluetoothAdapter.cancelDiscovery();
        }
    }

    public void enableBluetooth(){
        if(permissionsGranted) {
            if (this.bluetoothAdapter.isEnabled() == false) {
                Intent enableBlt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBlt, ENABLE_BLT_REQUEST);
            }
        }
    }

    public void addDevice(BluetoothDevice device) {
        if(device != null){
            this.deviceBuffer.add(device);

            for(BluetoothDeviceFoundListener listener : deviceFoundListeners){
                listener.deviceFound(device);
            }
        }
    }

    public void setPermssionGranted(boolean permssionGranted) {
        this.permissionsGranted = permssionGranted;
    }

    public void registerDeviceFoundListener(BluetoothDeviceFoundListener listener){
        if(listener != null){
            this.deviceFoundListeners.add(listener);
        }
    }

    public void unregisterDeviceFoundListener(BluetoothDeviceFoundListener listener){
        if(listener != null){
            this.deviceFoundListeners.remove(listener);
        }
    }
}
