package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

public class BluetoothActivity extends AppCompatActivity{
    BluetoothController controller;

    private ListView mDeviceList;
    private BluetoothArrayAdapter mDeviceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        getSupportActionBar().setTitle(getString(R.string.bluetooth_connect));

        controller = new BluetoothController(this);
        controller.getPermissions();

        mDeviceList = (ListView) findViewById(R.id.lvBluetoothDevices);
        mDeviceArrayAdapter = new BluetoothArrayAdapter(this,
                android.R.layout.simple_list_item_1, controller);
        mDeviceList.setAdapter(mDeviceArrayAdapter);

        controller.registerDeviceFoundListener(mDeviceArrayAdapter);
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
