package at.htl_leonding.musicnotesync.mainactivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.BaseController;
import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.blt.BltService;
import at.htl_leonding.musicnotesync.bluetooth.BluetoothActivity;
import at.htl_leonding.musicnotesync.bluetooth.listener.ServerListenerImpl;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryImpl;
import at.htl_leonding.musicnotesync.infrastructure.contract.Entity;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.infrastructure.contract.NotesheetImpl;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;
import at.htl_leonding.musicnotesync.infrastructure.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.infrastructure.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.mainactivity.adapter.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.mainactivity.listener.OpenAddDialogClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.management.ManagementOptionsClickListener;
import at.htl_leonding.musicnotesync.management.RenameNotesheetObjectDialog;
import at.htl_leonding.musicnotesync.management.move.MoveActivity;
import at.htl_leonding.musicnotesync.request.RequestCode;

/**
 * Created by michael on 11.08.16.
 */
public class MainController extends BaseController{
    private static final String TAG = MainController.class.getSimpleName();

    private MainModel mMainModel;
    private MainActivity mMainActivity;

    private NotesheetFacade notesheetFacade;
    private DirectoryFacade directoryFacade;

    public MainController(MainActivity activity){
        mMainActivity = activity;
        mMainModel = new MainModel(activity, this);
        directoryFacade = new DirectoryFacade(activity);
        notesheetFacade = new NotesheetFacade(activity);

        mMainModel.setCurrentDirectory(
                directoryFacade.getRootDirectory()
        );
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

        File file = mMainModel
                .getStorage()
                .copyFileToInternalStorage(this.mMainModel.getPhotoFile(), directory, null);

        Notesheet result = notesheetFacade.create(
                new File(directory + File.separator + file.getName()),
                mMainModel.getCurrentDirectory());

        return result;
    }

    public void openNotesheet(Notesheet notesheet) {
        Intent intent = new Intent(mMainActivity, BluetoothActivity.class);
        intent.putExtra(BluetoothActivity.OPERATION, BluetoothActivity.OPEN_NOTESHEET);
        intent.putExtra(BluetoothActivity.ENTITY_ID, notesheet.getId());
        mMainActivity.startActivity(intent);
    }

    public void startService() {
        mMainActivity.startService(new Intent(mMainActivity.getBaseContext(), BltService.class));


        if(BluetoothAdapter.getDefaultAdapter() != null &&
                PermissionHelper.getBluetoothPermissions(mMainActivity) == true) {
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.TAKE_PICTURE_REQUEST_CODE:
                    if(mMainModel.getPhotoFile() != null){
                        File tmpFile = mMainModel.getPhotoFile();

                        mMainModel.getStorage().copyFileToInternalStorage(
                                tmpFile, "camera", tmpFile.getName());

                        notesheetFacade.create(
                                new File("camera" + File.separator + tmpFile.getName()),
                                mMainModel.getCurrentDirectory());
                    }
                break;
                case RequestCode.SELECT_FILE_REQUEST_CODE:
                    if(data != null && data.getData() != null){
                        storeFileFromFileChooser(data.getData().getPath());
                    }
                break;
                case RequestCode.ADD_FOLDER_REQUEST_CODE:
                    if(data != null){
                        directoryFacade.create(
                                data.getStringExtra("FolderName"),
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
        mMainActivity.refreshNotesheetArrayAdapter();
    }

    public void openDirectory(Directory directory) {
        mMainModel.setCurrentDirectory(directory);
        mMainActivity.refreshNotesheetArrayAdapter();
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

    public void deleteNotesheetObject(Entity notesheetObject) {
        if(notesheetObject instanceof Directory){
            directoryFacade.delete((Directory)notesheetObject);
        }
        else if(notesheetObject instanceof Notesheet){
            notesheetFacade.delete((Notesheet)notesheetObject);
        }
    }

    public void renameNotesheetObject(String newName) {
        Object object = mMainModel.getObjectToRename();
        if(object instanceof Notesheet){
            NotesheetImpl notesheet = new NotesheetImpl();

            notesheet.fromNotesheet((Notesheet)object);
            notesheet.setName(newName);

            notesheetFacade.update(notesheet);
        }else if(object instanceof Directory){
            DirectoryImpl directory = new DirectoryImpl();

            directory.fromDirectory((Directory)object);
            directory.setName(newName);
            directoryFacade.update(directory);
        }
        refreshNotesheetArrayAdapter();
    }

    public void startRenameNotesheetObject(Object notesheetObject) {
        mMainModel.setObjectToRename(notesheetObject);
        RenameNotesheetObjectDialog dialog = new RenameNotesheetObjectDialog(mMainActivity, this);
        dialog.show();
    }

    public boolean goToDirectoryParent() {
        boolean currentDirectoryIsRoot =
                mMainModel.getCurrentDirectory().getId() ==
                        directoryFacade.getRootDirectory().getId();

        if(currentDirectoryIsRoot == false){
            mMainModel.setCurrentDirectory(
                    directoryFacade.getParent(
                            mMainModel.getCurrentDirectory())
            );

            openDirectory(mMainModel.getCurrentDirectory());
        }

        return !currentDirectoryIsRoot;
    }

    public List<Entity> getNotesheetObjects() {
        List<Entity> objects = new ArrayList<>();
        objects.addAll(
                directoryFacade.findByDirectory(mMainModel.getCurrentDirectory())
        );
        objects.addAll(
                notesheetFacade.findByDirectory(mMainModel.getCurrentDirectory())
        );
        return objects;
    }

    public void setPhotoFile(File photoFile) {
        mMainModel.setPhotoFile(photoFile);
    }
}
