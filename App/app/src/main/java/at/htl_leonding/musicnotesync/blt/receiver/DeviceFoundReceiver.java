package at.htl_leonding.musicnotesync.blt.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import at.htl_leonding.musicnotesync.blt.BltService;

/**
 * Created by michael on 3/11/17.
 */

public class DeviceFoundReceiver extends BroadcastReceiver {
    private final BltService mBltService;

    public DeviceFoundReceiver(BltService bltService){
        mBltService = bltService;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)){
            mBltService.deviceFound(
                    (BluetoothDevice) intent.getParcelableExtra(
                            BluetoothDevice.EXTRA_DEVICE));
            Toast.makeText(mBltService, "Device found", Toast.LENGTH_SHORT).show();
        }
    }
}
