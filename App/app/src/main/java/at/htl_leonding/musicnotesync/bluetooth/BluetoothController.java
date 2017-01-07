package at.htl_leonding.musicnotesync.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.connection.server.Server;
import at.htl_leonding.musicnotesync.bluetooth.connection.server.ServerManager;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothController {
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

    /**
     * Creates an instance of BluetoothController.
     * @param bluetoothActivity A BluetoothActivity-Instance the BluetoothController-Instance is
     *                          assigned to.
     */
    public BluetoothController(BluetoothActivity bluetoothActivity){
        mBluetoothActivity = bluetoothActivity;
        mModel = new BluetoothModel(mBluetoothActivity);
        mDeviceAdapter = new BluetoothDeviceAdapter(mBluetoothActivity);
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
                            enableDiscoverable();
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
            enableDiscoverable();
            if(bluetoothAdapter.isDiscovering() == false){
                startDiscovery();
            }
            return true;
        }
    }

    private void enableDiscoverable(){
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
        mBluetoothActivity.startActivity(discoverableIntent);
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
        BroadcastReceiver bluetoothEnabled = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    ServerManager.getInstance().startServer();
                }else{
//                    Server.getInstance().stopServer();
                }
            }
        };
        IntentFilter bltEnabledFilter =
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBluetoothActivity.registerReceiver(bluetoothEnabled, bltEnabledFilter);

        if(BluetoothAdapter.getDefaultAdapter() == null){
            mBluetoothActivity.setResult(Activity.RESULT_CANCELED);
            mBluetoothActivity.finish();
        }
        else if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
            ServerManager.getInstance().startServer();
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
}
