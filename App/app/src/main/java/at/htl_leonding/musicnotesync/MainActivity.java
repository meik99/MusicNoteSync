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

import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.helper.intent.CameraIntentHelper;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.mainactivity.listener.BluetoothBtnClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetLongClickListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnTempBluetooth;
    private RecyclerView mNoteSheetRecyclerView;
    private NotesheetArrayAdapter mAdapter;
    private MainController mController;
    private static MainController mStaticController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        mController = new MainController(this);
        mStaticController = mController;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mController.getFabListener());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new NotesheetArrayAdapter(mController, this);
        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.noteSheetRecyclerView);
        mNoteSheetRecyclerView.setAdapter(mAdapter);
        mNoteSheetRecyclerView.setLayoutManager(llm);
        //registerForContextMenu(mNoteSheetRecyclerView);



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
            case FabOnClickListener.ADD_FOLDER_REQUEST_CODE:
                if(data != null) {
                    String name = data.getStringExtra("FolderName");
                    mController.getDirectoryFacade().create(name);
                    mAdapter.setDirectory(mAdapter.getCurrentDirectory());
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case NotesheetLongClickListener.MOVE_DIRECTORY_REQUEST_CODE:
                Directory target = MoveFileActivity.getTargetDirectory();
                Directory source = NotesheetLongClickListener.getSourceDir();

                mController.getDirectoryFacade().move(source,target);
                mAdapter.setDirectory(mAdapter.getCurrentDirectory());
                mAdapter.notifyDataSetChanged();
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
        //mAdapter.setSheets(mController.getNotesheets(null));
        mController.dismissDialog();

        if(PermissionHelper.getBluetoothPermissions(this) == true){
            mController.tryStartBluetoothServer();
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.getCurrentDirectory().getParent() != null){
            mAdapter.setDirectory(mAdapter.getCurrentDirectory().getParent());
        }
        else {
            super.onBackPressed();
        }
    }



    public static MainController getMainController(){
        return mStaticController;
    }
}
