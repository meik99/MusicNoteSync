package at.htl_leonding.musicnotesync.mainactivity.listener;

import android.content.Intent;
import android.view.View;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothActivity;

/**
 * Created by michael on 22.08.16.
 */
public class BluetoothBtnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        Intent startBluetoothMenu = new Intent(view.getContext(), BluetoothActivity.class);
        view.getContext().startActivity(startBluetoothMenu);
    }
}
