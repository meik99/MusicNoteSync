package at.htl_leonding.musicnotesync;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import at.htl_leonding.musicnotesync.bluetooth.connection.Server;
import at.htl_leonding.musicnotesync.helper.intent.CameraIntentHelper;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.mainactivity.listener.BluetoothBtnClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnTempBluetooth;
    private RecyclerView mNoteSheetRecyclerView;
    private NotesheetArrayAdapter mAdapter;
    private MainController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        mController = new MainController(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mController.getFabListener());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new NotesheetArrayAdapter(mController);
        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.noteSheetRecyclerView);
        mNoteSheetRecyclerView.setAdapter(mAdapter);
        mNoteSheetRecyclerView.setLayoutManager(llm);

        mBtnTempBluetooth = (Button) findViewById(R.id.btnTempBluetooth);
        mBtnTempBluetooth.setOnClickListener(new BluetoothBtnClickListener());

        if(PermissionHelper.getBluetoothPermissions(this) == true){
            mController.tryStartBluetoothServer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CameraIntentHelper.REQUEST_CODE:
                mController.storeFileFromCameraIntent(resultCode);
                break;
            case FabOnClickListener.SELECT_FILE_REQUEST_CODE:
                if (data != null && data.getData() != null && data.getData().getPath() != null) {
                    mController.storeFileFromFileChooser(resultCode,
                            data.getData().getPath());
                }
                break;
        }

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
        mAdapter.setSheets(mController.getNotesheets(null));
        mController.dismissDialog();
        super.onResume();
    }

}
