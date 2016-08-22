package at.htl_leonding.musicnotesync.bluetooth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

public class BluetoothActivity extends AppCompatActivity {
    BluetoothController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        controller = new BluetoothController(this);
        controller.getPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PermissionHelper.STANDARD_PERMISSION_REQUEST_CODE){
            if(resultCode == PackageManager.PERMISSION_GRANTED){
                controller.setPermssionGranted(true);
                controller.enableBluetooth();
            }
        }
        else if (requestCode == BluetoothController.ENABLE_BLT_REQUEST){
            if(requestCode == RESULT_OK){
                controller.discoverDevices();
            }
        }
    }

    @Override
    protected void onResume() {
        controller.discoverDevices();
        super.onResume();
    }

    @Override
    protected void onPause() {
        controller.cancelDiscovery();
        super.onPause();
    }
}
