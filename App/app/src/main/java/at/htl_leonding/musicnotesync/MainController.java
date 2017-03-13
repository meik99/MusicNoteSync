package at.htl_leonding.musicnotesync;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import at.htl_leonding.musicnotesync.blt.BltService;
import at.htl_leonding.musicnotesync.bluetooth.BluetoothActivity;
import at.htl_leonding.musicnotesync.bluetooth.listener.ServerListenerImpl;
import at.htl_leonding.musicnotesync.bluetooth.socket.Server;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Entity;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.DirectoryImpl;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetImpl;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.management.ManagementOptionsClickListener;
import at.htl_leonding.musicnotesync.management.RenameNotesheetObjectDialog;
import at.htl_leonding.musicnotesync.management.move.MoveActivity;
import at.htl_leonding.musicnotesync.presentation.ImageViewActivity;
import at.htl_leonding.musicnotesync.request.RequestCode;

/**
 * Created by michael on 11.08.16.
 */
public class MainController implements Serializable, NotesheetFacade.NotesheetDbListener {
    private static final String TAG = MainController.class.getSimpleName();

    private MainModel mMainModel;
    private MainActivity mMainActivity;

    public MainController(MainActivity activity){
        mMainActivity = activity;
        mMainModel = new MainModel(activity, this);
        mMainModel.setCurrentDirectory(
                mMainModel.getDirectoryFacade().getRoot()
        );
        mMainModel.setFabOnClickListener(new FabOnClickListener(activity));
        mMainModel.setNotesheetItemClickListener(new NotesheetClickListener(this));
        mMainModel.setManagementOptionsClickListener(new ManagementOptionsClickListener(this));
        mMainModel.setNotesheetArrayAdapter(new NotesheetArrayAdapter(
                mMainModel.getNotesheetItemClickListener(),
                mMainModel.getManagementOptionClickListener()
        ));

        mMainModel.setServerListener(new ServerListenerImpl(mMainActivity));
        mMainModel.getServerListener().addNotesheetDbListener(this);
        refreshNotesheetArrayAdapter();

    }

    public View.OnClickListener getFabListener() {
        return this.mMainModel.getFabOnClickListener();
    }

    public Notesheet storeFileFromCameraIntent() {
        Log.d(TAG, "onActivityResult: Camera intent closed");
        Notesheet result = null;
        this.mMainModel.setPhotoFile(
                this.mMainModel.getFabOnClickListener().getPhotoFile()
        );

        if(this.mMainModel.getPhotoFile() != null &&
                this.mMainModel.getPhotoFile().exists()){
            result = storePhotoFile("camera");
        }

        return result;
    }

    public void storeFileFromFileChooser(String path) {
        String id = null;
        Cursor fileCursor = null;

        if(path != null){
            String[] pathData = path.split(":");
            if(pathData.length >= 2){
                id = pathData[1];
            }
        }

        if(id != null){
            String[] projection = new String[]{
                    MediaStore.Images.Media.DATA
            };
            String selector = MediaStore.Images.Media._ID + "=?";
            String[] selectorAgrs = new String[]{
                    id
            };
            fileCursor = mMainActivity.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selector,
                    selectorAgrs,
                    null
            );
        }


        if(fileCursor != null && fileCursor.moveToFirst() == true){
            String fullPath =
                    fileCursor.getString(
                            fileCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            File photoFile = new File(fullPath);
            this.mMainModel.setPhotoFile(photoFile);
        }

        if(this.mMainModel.getPhotoFile() != null &&
                this.mMainModel.getPhotoFile().exists()){
            Log.d(TAG, "storeFileFromFileChooser: Photo exists");
            storePhotoFile("file");
        }
    }

    private Notesheet storePhotoFile(String directory){
        Log.d(TAG, "storePhotoFile: Photo exists");

        mMainModel
                .getStorage()
                .copyFileToInternalStorage(this.mMainModel.getPhotoFile(), directory, null);

        Notesheet result = null;

        return mMainModel
                .getNotesheetFacade()
                .insert(null, directory, this.mMainModel.getPhotoFile().getName());

    }

    public void dismissDialog(){
        this.mMainModel.getFabOnClickListener().dismissDialog();
    }

    public List<Notesheet> getNotesheets(@Nullable Directory parent){
        return mMainModel.getNotesheetFacade().findByDirectory(parent);
    }

    public void openNotesheet(Notesheet notesheet) {
        Intent intent = new Intent(mMainActivity, BluetoothActivity.class);
        intent.putExtra(BluetoothActivity.OPERATION, BluetoothActivity.OPEN_NOTESHEET);
        intent.putExtra(BluetoothActivity.ENTITY_ID, notesheet.getId());
        mMainActivity.startActivity(intent);
//        Intent intent = new Intent(mMainActivity, ImageViewActivity.class);
//
//        intent.putExtra("pathName", notesheet.getPath());
//        mMainActivity.startActivity(intent);
    }

    public void startService() {
        mMainActivity.startService(new Intent(mMainActivity.getBaseContext(), BltService.class));


        if(BluetoothAdapter.getDefaultAdapter() != null &&
                PermissionHelper.getBluetoothPermissions(mMainActivity) == true) {
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    }

    public NotesheetArrayAdapter getNotesheetArrayAdapter() {
        return mMainModel.getNotesheetArrayAdapter();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.TAKE_PICTURE_REQUEST_CODE:
                    Notesheet notesheet = storeFileFromCameraIntent();
                    if (notesheet != null) {
                        mMainModel
                                .getNotesheetFacade()
                                .move(notesheet, mMainModel.getCurrentDirectory());
                    }
                break;
                case RequestCode.SELECT_FILE_REQUEST_CODE:
                    if(data != null){
                        storeFileFromFileChooser(data.getData().getPath());
                    }
                break;
                case RequestCode.ADD_FOLDER_REQUEST_CODE:
                    if(data != null){
                        DirectoryFacade directoryFacade = mMainModel.getDirectoryFacade();
                        directoryFacade.move(
                                directoryFacade.create(
                                        data.getStringExtra("FolderName")
                                ),
                                mMainModel.getCurrentDirectory()
                        );
                    }
                break;
                case RequestCode.MOVE_ITEM_REQUEST_CODE:
                    refreshNotesheetArrayAdapter();
                break;
            }
            refreshNotesheetArrayAdapter();
        }
    }

    public void refreshNotesheetArrayAdapter() {
        openDirectory(mMainModel.getCurrentDirectory());
    }

    public NotesheetClickListener getNotesheetItemClickListener() {
        return mMainModel.getNotesheetItemClickListener();
    }

    public void openDirectory(Directory directory) {
        mMainModel.setCurrentDirectory(directory);
        mMainModel
                .getNotesheetArrayAdapter()
                .setNotesheetObjects(
                        mMainModel.getDirectoryChildren(directory)
                );
    }

    public ManagementOptionsClickListener getManagementOptionClickListener() {
        return mMainModel.getManagementOptionClickListener();
    }

    public void startMoveObjectToDirectory(Object notesheetObject) {
        Intent intent = new Intent(mMainActivity, MoveActivity.class);
        long id = -1;
        String _class = new String();

        if(notesheetObject instanceof Entity){
            Entity entity = (Entity)notesheetObject;
            id = entity.getId();

            if(notesheetObject instanceof Notesheet){
                _class = "Notesheet";
            }else if(notesheetObject instanceof Directory){
                _class = "Directory";
            }
        }

        intent.putExtra("ObjectId", id);
        intent.putExtra("ObjectClass", _class);

        mMainActivity.startActivityForResult(
                intent, RequestCode.MOVE_ITEM_REQUEST_CODE);

    }

    public void deleteNotesheetObject(Object notesheetObject) {
        if(notesheetObject instanceof Directory){
            mMainModel.getDirectoryFacade().delete((Directory) notesheetObject);
        }
        else if(notesheetObject instanceof Notesheet){
            mMainModel.getNotesheetFacade().delete((Notesheet)notesheetObject);
        }
    }

    public void renameNotesheetObject(String newName) {
        Object object = mMainModel.getObjectToRename();
        if(object instanceof Notesheet){
            NotesheetImpl notesheet = new NotesheetImpl();

            notesheet.fromNotesheet((Notesheet)object);
            notesheet.setName(newName);

            mMainModel.getNotesheetFacade().update((notesheet));
        }else if(object instanceof Directory){
            DirectoryImpl directory = new DirectoryImpl();

            directory.fromDirectory((Directory)object);
            directory.setName(newName);
            mMainModel.getDirectoryFacade().update(directory);
        }
        refreshNotesheetArrayAdapter();
    }

    public void startRenameNotesheetObject(Object notesheetObject) {
        mMainModel.setObjectToRename(notesheetObject);
        RenameNotesheetObjectDialog dialog = new RenameNotesheetObjectDialog(mMainActivity, this);
        dialog.show();
    }

    public boolean goToDirectoryParent() {
        if(mMainModel.getCurrentDirectory().getParent() != null){
            openDirectory(
                    mMainModel.getCurrentDirectory().getParent()
            );
            return true;
        }
        return false;
    }

    @Override
    public void onNotesheetInserted(final Notesheet notesheet) {
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshNotesheetArrayAdapter();

                if(notesheet != null && notesheet.getId() > 0) {
                    Snackbar.make(
                            mMainActivity.findViewById(
                                    R.id.mainLayout),
                            R.string.transfer_successful,
                            Snackbar.LENGTH_LONG
                    )
                            .show();
                }else{
                    Snackbar.make(
                            mMainActivity.findViewById(
                                    R.id.mainLayout),
                            R.string.transfer_unsuccessful,
                            Snackbar.LENGTH_LONG
                    )
                            .show();
                }
            }
        });
    }
}
