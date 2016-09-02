package at.htl_leonding.musicnotesync.bluetooth.communication;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothController;
import at.htl_leonding.musicnotesync.bluetooth.client.BluetoothClientController;
import at.htl_leonding.musicnotesync.bluetooth.server.BluetoothServerController;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 02.09.16.
 */
public class BluetoothCommunicator {
    private static BluetoothCommunicator instance;
    private static boolean initialized = false;

    private BluetoothController mController;
    private BluetoothServerController mServerController;
    private BluetoothClientController mClientController;
    private BluetoothCommunicator(
            BluetoothController bluetoothController,
            BluetoothServerController serverModel,
            BluetoothClientController clientModel){

        mController = bluetoothController;
        mServerController = serverModel;
        mClientController = clientModel;
        initialized = true;
    }

    public BluetoothServerController getServerController() {
        return mServerController;
    }

    public void setServerController(BluetoothServerController mServerModel) {
        this.mServerController = mServerModel;
    }

    public BluetoothClientController getClientController() {
        return mClientController;
    }

    public void setClientController(BluetoothClientController mClientModel) {
        this.mClientController = mClientModel;
    }

    public static void init(
            BluetoothController bluetoothController,
            BluetoothServerController bluetoothServerModel,
            BluetoothClientController bluetoothClientModel) {
        if (initialized == false) {
            instance = new BluetoothCommunicator(bluetoothController,
                    bluetoothServerModel, bluetoothClientModel);
        } else {
            if (bluetoothClientModel != null) {
                instance.setClientController(bluetoothClientModel);
            }
            if (bluetoothServerModel != null) {
                instance.setServerController(bluetoothServerModel);
            }
            if (bluetoothController != null) {
                instance.setController(bluetoothController);
            }
        }
    }

    public static boolean isInitialized(){
        return initialized;
    }

    public static BluetoothCommunicator getInstance(){
        if(instance == null || initialized == false){
            throw new IllegalStateException("Communcator hasn't been initialized yet");
        }
        return instance;
    }

    public void openNotesheet(Notesheet ns){
        if(mController.isServer() == true){
            //Send file to clients
            mServerController.sendNotesheetToClients(ns);
        }
    }

    public void setController(BluetoothController controller) {
        this.mController = controller;
    }
}
