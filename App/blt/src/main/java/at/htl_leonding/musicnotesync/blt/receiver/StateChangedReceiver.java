package at.htl_leonding.musicnotesync.blt.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import at.htl_leonding.musicnotesync.blt.BltService;

/**
 * Created by michael on 3/11/17.
 */

public class StateChangedReceiver extends BroadcastReceiver{
    private final BltService mBltService;

    public StateChangedReceiver(BltService bltService){
        mBltService = bltService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra(BluetoothAdapter.EXTRA_STATE)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            if(state == BluetoothAdapter.STATE_ON){
                mBltService.setBluetoothState(BluetoothState.ON);
            }else if(state == BluetoothAdapter.STATE_TURNING_OFF
                    || state == BluetoothAdapter.STATE_OFF){
                mBltService.setBluetoothState(BluetoothState.OFF);
            }
        }
    }
}
