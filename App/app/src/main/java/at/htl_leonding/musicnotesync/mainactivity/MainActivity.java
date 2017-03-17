package at.htl_leonding.musicnotesync.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

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
    private FloatingActionButton mOpenAddDDialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mController = new MainController(this);
        mOpenAddDDialogButton = (FloatingActionButton) findViewById(R.id.fab);
        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.noteSheetRecyclerView);


        mOpenAddDDialogButton.setOnClickListener(new OpenAddDialogClickListener(this));
        mNoteSheetRecyclerView.setAdapter(new NotesheetArrayAdapter(
                new NotesheetClickListener(mController),
                new ManagementOptionsClickListener(mController)
        ));
        mNoteSheetRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
        //mAdapter.setSheets(mController.getNotesheets(null));
        mController.dismissDialog();

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
}
