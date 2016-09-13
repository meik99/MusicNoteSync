package at.htl_leonding.musicnotesync.bluetooth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

public class BluetoothActivity extends AppCompatActivity{
    private BluetoothController mController;
    private ListView mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        getSupportActionBar().setTitle(getString(R.string.bluetooth_connect));

        mDeviceList = (ListView) findViewById(R.id.lvBluetoothDevices);
        mDeviceList.setAdapter(null);
        mController = new BluetoothController(this);

        if(mController.getBluetoothPermissions() == true){
            mController.enableBluetooth();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode ==
                at.htl_leonding.musicnotesync.bluetooth.deprecated.BluetoothController
                        .ENABLE_BLT_REQUEST){
            if(resultCode == RESULT_OK){
                activateBluetooth();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if(requestCode == PermissionHelper.STANDARD_PERMISSION_REQUEST_CODE){
            boolean allGranted = true;

            for(int resultCode : grantResults){
                if(resultCode == PackageManager.PERMISSION_DENIED){
                    allGranted = false;
                }
            }

            if(allGranted == true){
                mController.enableBluetooth();
            }else{
                this.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        activateBluetooth();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //controller.cancelDiscovery();
        //controller.stopServer();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mController.stop();
    }

    private void activateBluetooth(){
        /*if(controller.hasPermissions() == true) {
            if(controller.isBluetoothEnabled() == false){
                controller.enableBluetooth();
            }else if(controller.isDiscovering() == false){
                controller.discoverDevices();

                if(controller.isDiscoverable() == false){
                    controller.enableDiscoverability();
                }

                if(controller.hasServerStarted() == false) {
                    controller.startServer();
                }
            }
        }
        else{
            controller.getPermissions();
        }*/
    }
}
