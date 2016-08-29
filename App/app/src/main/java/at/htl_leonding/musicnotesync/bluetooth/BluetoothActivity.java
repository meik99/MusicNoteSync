package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
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
        controller.enableDiscoverability();
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
                controller.startServer();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.controller.discoverDevices();
        controller.startServer();
    }

    @Override
    protected void onResume() {
        controller.discoverDevices();
        controller.startServer();
        super.onResume();
    }

    @Override
    protected void onPause() {
        controller.cancelDiscovery();
        controller.stopServer();
        super.onPause();
    }

    @Override
    protected void onStop() {
        controller.cancelDiscovery();
        controller.stopServer();
        super.onStop();
    }
}
