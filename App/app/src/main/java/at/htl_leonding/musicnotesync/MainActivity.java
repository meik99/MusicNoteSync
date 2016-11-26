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
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.helper.EmergencyStorage;
import at.htl_leonding.musicnotesync.helper.intent.CameraIntentHelper;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.mainactivity.listener.BluetoothBtnClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetLongClickListener;
import at.htl_leonding.musicnotesync.management.MoveActivity;
import at.htl_leonding.musicnotesync.request.RequestCode;

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

        mAdapter = new NotesheetArrayAdapter(mController, this);
        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.noteSheetRecyclerView);
        mNoteSheetRecyclerView.setAdapter(mAdapter);
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
        long id = 0;
        switch (requestCode) {
            case CameraIntentHelper.REQUEST_CODE:
                NotesheetFacade notesheetFacade = new NotesheetFacade(this);
                Notesheet newNotesheet = mController.storeFileFromCameraIntent(resultCode);
                notesheetFacade.move(newNotesheet, mAdapter.getCurrentDirectory());
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
                    Directory newDirectory =
                        mController.getDirectoryFacade().create(name);

                    mController.getDirectoryFacade().move(
                            newDirectory, mAdapter.getCurrentDirectory());
                }
                break;
            case RequestCode.MOVE_DIRECTORY_REQUEST_CODE:
                id = EmergencyStorage.id;
                if (id != -1) {
                    DirectoryFacade df = new DirectoryFacade(this);
                    Directory source = df.findById(id);
                    Directory target = MoveActivity.getTargetDirectory();
                    mController.getDirectoryFacade().move(source, target);
                }
                break;
            case RequestCode.MOVE_NOTESHEET_REQUEST_CODE:

                id = EmergencyStorage.id;
                if (id != -1) {
                    NotesheetFacade nf = new NotesheetFacade(this);
                    Notesheet source = nf.findById(id);
                    Directory target = MoveActivity.getTargetDirectory();

                    nf.move(source, target);
                }

                break;

        }

        mAdapter.refresh();
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
