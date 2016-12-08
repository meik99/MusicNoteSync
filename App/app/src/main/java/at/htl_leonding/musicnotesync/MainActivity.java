package at.htl_leonding.musicnotesync;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.mainactivity.listener.BluetoothBtnClickListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnTempBluetooth;
    private RecyclerView mNoteSheetRecyclerView;
    private MainController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        mController = new MainController(this);
        fab.setOnClickListener(mController.getFabListener());

        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.noteSheetRecyclerView);
        mNoteSheetRecyclerView.setAdapter(mController.getNotesheetArrayAdapter());
        mNoteSheetRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));




        mBtnTempBluetooth = (Button) findViewById(R.id.btnTempBluetooth);
        mBtnTempBluetooth.setOnClickListener(new BluetoothBtnClickListener());

    }

    @Override
    protected void onPause() {
        mController.unregisterBluetoothFilter();

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mController.handleActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        //mAdapter.setSheets(mController.getNotesheets(null));
        mController.dismissDialog();

        if(PermissionHelper.getBluetoothPermissions(this) == true){
            mController.tryStartBluetoothServer();
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mController.goToDirectoryParent() == false){
            super.onBackPressed();
        }
    }
}
