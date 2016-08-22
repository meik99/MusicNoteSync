package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

/**
 * Created by michael on 17.08.16.
 */
public class BluetoothController{
    public static final int ENABLE_BLT_REQUEST = 6;

    private List<BluetoothDevice> mDeviceBuffer = new LinkedList<>();
    private List<BluetoothDeviceFoundListener> mDeviceFoundListeners = new LinkedList<>();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothActivity mActivity;
    private BluetoothDeviceReciever mBluetoothDeviceReciever;
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
        PermissionHelper.verifyPermissions(mActivity, new String[]{
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN
        });
    }

    public void discoverDevices(){
        if(mPermissionsGranted) {
            IntentFilter bltFoundDeviceFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mActivity.registerReceiver(this.mBluetoothDeviceReciever, bltFoundDeviceFilter);

            this.mDeviceBuffer.clear();
            this.mBluetoothAdapter.startDiscovery();
        }
    }

    public void cancelDiscovery(){
        if(mPermissionsGranted) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
    }

    public void enableBluetooth(){
        if(mPermissionsGranted) {
            if (this.mBluetoothAdapter.isEnabled() == false) {
                Intent enableBlt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBlt, ENABLE_BLT_REQUEST);
            }
        }
    }

    public void addDevice(BluetoothDevice device) {
        if(device != null){
            this.mDeviceBuffer.add(device);

            for(BluetoothDeviceFoundListener listener : mDeviceFoundListeners){
                listener.deviceFound(device);
            }
        }
    }

    public List<BluetoothDevice> getDevices(){
        return mDeviceBuffer != null ? mDeviceBuffer : new LinkedList<BluetoothDevice>();
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
