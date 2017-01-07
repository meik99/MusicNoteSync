package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothModel{
    private final List<BluetoothDevice> mDevices;
    private NotesheetFacade notesheetFacade;
    private DirectoryFacade directoryFacade;
    private Context mContext;

    public BluetoothModel(BluetoothActivity bluetoothActivity){
        mDevices = new LinkedList<>();
        mContext = bluetoothActivity;
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

    public NotesheetFacade getNotesheetFacade() {
        if(notesheetFacade == null){
            notesheetFacade = new NotesheetFacade(mContext);
        }
        return notesheetFacade;
    }

    public DirectoryFacade getDirectoryFacade() {
        if(directoryFacade == null){
            directoryFacade = new DirectoryFacade(mContext);
        }
        return directoryFacade;
    }
}
