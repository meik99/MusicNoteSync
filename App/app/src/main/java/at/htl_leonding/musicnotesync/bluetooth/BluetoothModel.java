package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.listener.ServerListenerImpl;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothModel{
    private final List<BluetoothDevice> mDevices;
    private final List<BluetoothDevice> mSelectedDevices;
    private NotesheetFacade notesheetFacade;
    private DirectoryFacade directoryFacade;
    private Context mContext;
    private ServerListenerImpl serverListener;
    private BluetoothDeviceAdapter mDeviceAdapter;
    private Notesheet activeNotesheet;

    public BluetoothModel(BluetoothActivity bluetoothActivity){
        mDevices = new LinkedList<>();
        mSelectedDevices = new LinkedList<>();
        mContext = bluetoothActivity;
    }

    public List<BluetoothDevice> addBluetoothDevice(BluetoothDevice device){
        return addBluetoothDeviceToList(device, mDevices);
    }

    public List<BluetoothDevice> addSelectedBluetoothDevice(BluetoothDevice device){
        return addBluetoothDeviceToList(device, mSelectedDevices);
    }

    private List<BluetoothDevice> addBluetoothDeviceToList(BluetoothDevice device,
                                                           List<BluetoothDevice> list){
        if(device != null){
            boolean listContainsDevice = false;

            for (BluetoothDevice bd : list){
                if(bd.getAddress().equals(device.getAddress())){
                    listContainsDevice = true;
                }
            }

            if(listContainsDevice == false){
                list.add(device);
            }
        }
        List<BluetoothDevice> devicesCopy = new LinkedList<>();
        devicesCopy.addAll(list);
        return devicesCopy;
    }

    public List<BluetoothDevice> removeSelectedBluetoothDevice(BluetoothDevice device){
        if(device != null){
            BluetoothDevice deviceToRemove = null;

            for(int i = 0; i < mSelectedDevices.size(); i++){
                BluetoothDevice bluetoothDevice = mSelectedDevices.get(i);
                if(bluetoothDevice.getAddress().equals(device.getAddress())){
                    deviceToRemove = bluetoothDevice;
                }
            }

            mSelectedDevices.remove(deviceToRemove);
        }

        List<BluetoothDevice> devicesCopy = new LinkedList<>();
        devicesCopy.addAll(mSelectedDevices);
        return devicesCopy;
    }

    public List<BluetoothDevice> getSelectedBluetoothDevices(){
        List<BluetoothDevice> devicesCopy = new LinkedList<>();
        devicesCopy.addAll(mSelectedDevices);
        return devicesCopy;
    }

    public boolean selectedDeviceListContainsDevice(BluetoothDevice device){
        for (BluetoothDevice bluetoothDevice: mSelectedDevices){
            if(bluetoothDevice.getAddress().equals(device.getAddress())){
                return true;
            }
        }
        return false;
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

    public void setServerListener(ServerListenerImpl serverListener) {
        this.serverListener = serverListener;
    }

    public ServerListenerImpl getServerListener() {
        return serverListener;
    }

    public void setDeviceAdapter(BluetoothDeviceAdapter deviceAdapter) {
        this.mDeviceAdapter = deviceAdapter;
    }

    public BluetoothDeviceAdapter getmDeviceAdapter() {
        return mDeviceAdapter;
    }

    public void setmDeviceAdapter(BluetoothDeviceAdapter mDeviceAdapter) {
        this.mDeviceAdapter = mDeviceAdapter;
    }

    public BluetoothDeviceAdapter getDeviceAdapter() {
        return mDeviceAdapter;
    }

    public void setActiveNotesheet(Notesheet activeNotesheet) {
        this.activeNotesheet = activeNotesheet;
    }

    public Notesheet getActiveNotesheet() {
        return activeNotesheet;
    }
}
