package at.htl_leonding.musicnotesync.mainactivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.mainactivity.adapter.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.OpenAddDialogClickListener;
import at.htl_leonding.musicnotesync.management.ManagementOptionsClickListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mNoteSheetRecyclerView;
    private MainController mController;
    private FloatingActionButton mOpenAddDialogButton;

    private NotesheetArrayAdapter mNotesheetArrayAdapter;
    private CheckBox mChkMakeDiscoverable;


    private BroadcastReceiver mBltScanModeChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int intentState =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
            if(intentState !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                mChkMakeDiscoverable.setChecked(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mController = new MainController(this);
        mOpenAddDialogButton = (FloatingActionButton) findViewById(R.id.fab);
        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.noteSheetRecyclerView);
        mNotesheetArrayAdapter = new NotesheetArrayAdapter(
                new NotesheetClickListener(mController),
                new ManagementOptionsClickListener(mController)
        );
        mChkMakeDiscoverable = (CheckBox) findViewById(R.id.chkMakeDiscoverable);

        mOpenAddDialogButton.setOnClickListener(new OpenAddDialogClickListener(this, mController));
        mNoteSheetRecyclerView.setAdapter(mNotesheetArrayAdapter);
        mNoteSheetRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mChkMakeDiscoverable.setChecked(
                BluetoothAdapter.getDefaultAdapter() != null &&
                BluetoothAdapter.getDefaultAdapter().getScanMode() ==
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        mChkMakeDiscoverable.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentFilter bltScanModeChanged =
                                new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

                        registerReceiver(mBltScanModeChange, bltScanModeChanged);

                        Intent discoverableIntent =
                                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(
                                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        startActivity(discoverableIntent);
                    }
                }
        );

        refreshNotesheetArrayAdapter();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mController.handleActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PermissionHelper.STANDARD_PERMISSION_REQUEST_CODE){
            boolean allGranted = true;
            for (int result :
                    grantResults) {
                if(result == PackageManager.PERMISSION_DENIED){
                    allGranted = false;
                }
            }

            if(allGranted){
                mController.startService();
            }
        }
    }

    @Override
    protected void onResume() {
        if(PermissionHelper.getBluetoothPermissions(this) == true){
            mController.startService();
        }

        mController.refreshNotesheetArrayAdapter();

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mController.goToDirectoryParent() == false){
            super.onBackPressed();
        }
    }

    public void refreshNotesheetArrayAdapter(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNotesheetArrayAdapter.setNotesheetObjects(
                        mController.getNotesheetObjects()
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mBltScanModeChange);
        }catch(IllegalArgumentException e){
            //Not catching because bullshit
        }
        super.onDestroy();
    }
}
