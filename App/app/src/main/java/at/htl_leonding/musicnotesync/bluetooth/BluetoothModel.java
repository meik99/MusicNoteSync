package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothModel{
    private final List<BluetoothDevice> mDevices;

    public BluetoothModel(){
        mDevices = new LinkedList<>();
    }

    public List<BluetoothDevice> addBluetoothDevice(BluetoothDevice device){
        if(device != null){
            boolean listContainsDevice = false;

            for (BluetoothDevice bd : mDevices){
                if(bd.getAddress().equals(device.getAddress())){
                    listContainsDevice = true;
                }
            }

            if(listContainsDevice == false){
                mDevices.add(device);
            }
        }
        List<BluetoothDevice> devicesCopy = new LinkedList<>();
        devicesCopy.addAll(mDevices);
        return devicesCopy;
    }
}
