package at.htl_leonding.musicnotesync.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.socket.Client;
import at.htl_leonding.musicnotesync.bluetooth.socket.Server;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothController implements Server.ServerListener, Client.ClientListener {
    private final static String TAG = BluetoothController.class.getSimpleName();

    private final BluetoothActivity mBluetoothActivity;
    private final BluetoothModel mModel;
    private final BluetoothDeviceAdapter mDeviceAdapter;

    private final BroadcastReceiver mDeviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            mDeviceAdapter.setDataSet(
                    mModel.addBluetoothDevice(foundDevice));
        }
    };

    private final BroadcastReceiver bluetoothEnabled = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    == BluetoothAdapter.STATE_ON) {
                Server.getInstance().startServer();
                Server.getInstance().addListener(BluetoothController.this);
            }else{
                Server.getInstance().stopServer();
                Server.getInstance().removeListener(BluetoothController.this);
            }
        }
    };

    /**
     * Creates an instance of BluetoothController.
     * @param bluetoothActivity A BluetoothActivity-Instance the BluetoothController-Instance is
     *                          assigned to.
     */
    public BluetoothController(BluetoothActivity bluetoothActivity){
        mBluetoothActivity = bluetoothActivity;
        mModel = new BluetoothModel(mBluetoothActivity);
        mDeviceAdapter = new BluetoothDeviceAdapter(mBluetoothActivity, this);
        ((ListView)mBluetoothActivity.findViewById(R.id.lvBluetoothDevices))
                .setAdapter(mDeviceAdapter);
    }

    /**
     * Checks if device has bluetooth functionality. If functionality is given,
     * obtains a BluetoothAdapter and enables Bluetooth. <u><b>Does not</b></u> ask for
     * permissions.
     * Asks for enabling discoverable and then starts discovering devices.
     * @return true if Bluetooth has been enabled, else false
     */
    public boolean enableBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            showToast(R.string.bluetooth_no_support);
            return false;
        }

        if(bluetoothAdapter.isEnabled() == false){
            if(bluetoothAdapter.enable() == true){
                BroadcastReceiver btStateChanged = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) ==
                                BluetoothAdapter.STATE_ON) {
                            context.unregisterReceiver(this);
                            enableDiscoverable(mBluetoothActivity);
                            startDiscovery();
                        }
                    }
                };
                IntentFilter bsStateChangedFilter =
                        new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

                mBluetoothActivity.registerReceiver(btStateChanged, bsStateChangedFilter);
                showToast(R.string.bluetooth_enabled);
                return true;
            }else{
                showToast(R.string.bluetooth_not_enabled);
                return false;
            }
        }else{
            showToast(R.string.bluetooth_enabled);
            enableDiscoverable(mBluetoothActivity);
            if(bluetoothAdapter.isDiscovering() == false){
                startDiscovery();
            }
            return true;
        }
    }

    public static void enableDiscoverable(Context context){
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
        context.startActivity(discoverableIntent);
    }

    private void startDiscovery(){
        BluetoothAdapter bluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            IntentFilter deviceFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mBluetoothActivity.registerReceiver(mDeviceFoundReceiver, deviceFoundFilter);
            bluetoothAdapter.startDiscovery();
        }
    }

    private void showToast(@StringRes int stringRes){
        Toast.makeText(mBluetoothActivity, stringRes, Toast.LENGTH_LONG)
                .show();
    }

    /**
     * Call when BluetoothActivity calls onStop() to clean up and
     * prevent memory leaking objects.
     */
    public void stop(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
        }
        try {
            mBluetoothActivity.unregisterReceiver(mDeviceFoundReceiver);
        }catch (IllegalArgumentException ex){
            Log.i(TAG, "stop: " + ex.getMessage());
        }
    }

    public BroadcastReceiver startServer() {
        IntentFilter bltEnabledFilter =
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        try{
            mBluetoothActivity.unregisterReceiver(bluetoothEnabled);
        }catch(Exception e) {
            //Ignore; just testing if register is already registered
        }
        mBluetoothActivity.registerReceiver(bluetoothEnabled, bltEnabledFilter);

        if(BluetoothAdapter.getDefaultAdapter() == null){
            mBluetoothActivity.setResult(Activity.RESULT_CANCELED);
            mBluetoothActivity.finish();
        }
        else if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Server.getInstance().startServer();
            Server.getInstance().addListener(this);
        }

        return bluetoothEnabled;
    }

    public View.OnClickListener getOnClickListener() {
        Intent activityIntent = mBluetoothActivity.getIntent();
        long entityId = activityIntent.getLongExtra(BluetoothActivity.ENTITY_ID, -1);

        if(activityIntent.hasExtra(BluetoothActivity.OPERATION) && entityId != -1){
            long operationId = activityIntent.getLongExtra(BluetoothActivity.OPERATION, -1);

            if(operationId == BluetoothActivity.SEND_NOTESHEET){

                return new BluetoothSendNotesheetClickListener(
                        this,
                        mModel.getNotesheetFacade().findById(entityId));
            }
        }

        Toast.makeText(mBluetoothActivity, R.string.error_creating_activity, Toast.LENGTH_SHORT)
                .show();
        mBluetoothActivity.finish();

        return null;
    }


    public int getActionButtonText() {
        Intent activityIntent = mBluetoothActivity.getIntent();

        if(activityIntent.hasExtra(BluetoothActivity.OPERATION)){
            long operationId = activityIntent.getLongExtra(BluetoothActivity.OPERATION, -1);

            if(operationId == BluetoothActivity.SEND_NOTESHEET){
                return R.string.send_notesheet;
            }
        }
        return R.string.error_creating_activity;
    }

    public void toggleDevice(BluetoothDevice bluetoothDevice) {
        if(mModel.selectedDeviceListContainsDevice(bluetoothDevice)){
            mModel.removeSelectedBluetoothDevice(bluetoothDevice);
        }else{
            mModel.addSelectedBluetoothDevice(bluetoothDevice);
        }
    }

    public void sendNotesheetMetadata(Notesheet notesheet) {
        List<BluetoothDevice> devices = mModel.getSelectedBluetoothDevices();
        Client client = new Client();
        client.addListener(this);
        for (BluetoothDevice bluetoothDevice : devices){
            client.connect(bluetoothDevice);
            client.sendMessage("Test");
            //client.disconnect();
        }
    }

    @Override
    public void onServerDeviceConnected(BluetoothSocket socket) {
        Toast
                .makeText(mBluetoothActivity,
                        "Server connected to " + socket.getRemoteDevice().getName(),
                        Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onServerMessageReceived(BluetoothSocket socket, String message) {
        Toast
                .makeText(mBluetoothActivity,
                        "Server received message " +
                                message +
                                "from" +
                                socket.getRemoteDevice().getName(),
                        Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClientConnected(BluetoothSocket socket) {
        Toast
                .makeText(mBluetoothActivity,
                        "Client connected to " + socket.getRemoteDevice().getName(),
                        Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClientMessageReceived(BluetoothSocket socket, String message) {
        Toast
                .makeText(mBluetoothActivity,
                        "Client received message " +
                                message +
                                "from" +
                                socket.getRemoteDevice().getName(),
                        Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClientDisconnected(BluetoothSocket socket) {
        Toast
                .makeText(mBluetoothActivity,
                        "Client disconnected to " + socket.getRemoteDevice().getName(),
                        Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onServerDeviceDisconnected(BluetoothSocket socket) {
        Toast
                .makeText(mBluetoothActivity,
                        "Server disconnected from " + socket.getRemoteDevice().getName(),
                        Toast.LENGTH_SHORT)
                .show();
    }
}
