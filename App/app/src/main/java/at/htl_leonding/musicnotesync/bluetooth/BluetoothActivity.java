package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.blt.BltRepository;
import at.htl_leonding.musicnotesync.blt.BltService;
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
    private FloatingActionButton mBtnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        getSupportActionBar().setTitle(getString(R.string.bluetooth_connect));

        mController = new BluetoothController(this);
        mDeviceList = (ListView) findViewById(R.id.lvBluetoothDevices);
        mBtnAction = (Button) findViewById(R.id.btnBluetoothAction);
        mBtnRefresh = (FloatingActionButton) findViewById(R.id.btnRefresh);

        mBtnAction.setText(mController.getActionButtonText());
        mBtnAction.setOnClickListener(mController.getOnClickListener());

        mDeviceList.setAdapter(new BluetoothDeviceAdapter(this, mController));

        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                BltRepository.getInstance().refresh();
                BluetoothAdapter.getDefaultAdapter().startDiscovery();
                mDeviceList.setAdapter(new BluetoothDeviceAdapter(BluetoothActivity.this, mController));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
