package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

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
        mModel = new BluetoothModel();
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
            startDiscovery();
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

    /**
     * Verifies permissions needed for bluetooth functionality.
     * Requests them if it does not have them.
     * Uses PermissionHelper.STANDARD_REQUEST_CODE for permission request.
     * @return true if app has permissions, else false
     */
    public boolean getBluetoothPermissions(){
        boolean hasPermission = PermissionHelper.verifyPermissions(
                mBluetoothActivity,
                BluetoothConstants.PERMISSIONS);
        return hasPermission;
    }
}
