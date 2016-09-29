package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.connection.Server;
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

        if(PermissionHelper.getBluetoothPermissions(this) == true){
            boolean bluetoothActivated = mController.enableBluetooth();

            if(bluetoothActivated == true){
                if(Server.getInstance().isRunning() == false) {
                    Server.getInstance().startServer();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
}
