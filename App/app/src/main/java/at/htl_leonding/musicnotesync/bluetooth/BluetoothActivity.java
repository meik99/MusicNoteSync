package at.htl_leonding.musicnotesync.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

public class BluetoothActivity extends AppCompatActivity{
    private static final String TAG = BluetoothActivity.class.getSimpleName();

    public static final String OPERATION = "OPERATION";
    public static final long SEND_NOTESHEET = 0;
    public static final long OPEN_NOTESHEET = 1;
    public static final String ENTITY_ID = "ENTITY_ID";

    private BluetoothController mController;

    private ListView mDeviceList;
    private Button mBtnAction;
    private RelativeLayout mLoadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        getSupportActionBar().setTitle(getString(R.string.bluetooth_connect));

        mController = new BluetoothController(this);
        mDeviceList = (ListView) findViewById(R.id.lvBluetoothDevices);
        mBtnAction = (Button) findViewById(R.id.btnBluetoothAction);

        mDeviceList.setAdapter(mController.getDeviceAdapter());
        mBtnAction.setOnClickListener(mController.getOnClickListener());
        mBtnAction.setText(mController.getActionButtonText());

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

        if(PermissionHelper.getBluetoothPermissions(this) == true){
            mController.enableBluetooth();
        }
    }

    @Override
    protected void onPause() {
        //controller.cancelDiscovery();
        //controller.stopServer();
        mController.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
