package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.StringRes;
import android.widget.Toast;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothController {
    private final BluetoothActivity mBluetoothActivity;

    /**
     * Creates an instance of BluetoothController.
     * @param bluetoothActivity A BluetoothActivity-Instance the BluetoothController-Instance is
     *                          assigned to.
     */
    public BluetoothController(BluetoothActivity bluetoothActivity){
        mBluetoothActivity = bluetoothActivity;
    }

    /**
     * Checks if device has bluetooth functionality. If functionality is given,
     * obtains a BluetoothAdapter and enables Bluetooth. <u><b>Does not</b></u> ask for
     * permissions.
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
                showToast(R.string.bluetooth_enabled);
                return true;
            }else{
                showToast(R.string.bluetooth_not_enabled);
                return false;
            }
        }else{
            showToast(R.string.bluetooth_enabled);
            return true;
        }
    }

    private void showToast(@StringRes int stringRes){
        Toast.makeText(mBluetoothActivity, stringRes, Toast.LENGTH_LONG)
                .show();
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
