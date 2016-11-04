package at.htl_leonding.musicnotesync;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.connection.server.ServerManager;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.io.Storage;
import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;

/**
 * Created by michael on 11.08.16.
 */
public class MainController {
    private static final String TAG = MainController.class.getSimpleName();

    private MainModel model;

    private  BroadcastReceiver mBtStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int bltState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if(bltState == BluetoothAdapter.STATE_ON) {
                ServerManager.getInstance().startServer();
            }else{
//                            Server.getInstance().stopServer();
            }
        }
    };

    public MainController(MainActivity activity){
        this.model = new MainModel();
        this.model.setActivity(activity);
        this.model.setListener(new FabOnClickListener(this.model.getActivity()));
    }

    public View.OnClickListener getFabListener() {
        return this.model.getListener();
    }

    public void storeFileFromCameraIntent(int resultCode) {
        Log.d(TAG, "onActivityResult: Camera intent closed");
        this.model.setPhotoFile(
                this.model.getListener().getPhotoFile()
        );

        if(resultCode == Activity.RESULT_OK &&
                this.model.getPhotoFile() != null &&
                this.model.getPhotoFile().exists()){
            storePhotoFile("camera");
        }
    }

    public void storeFileFromFileChooser(int resultCode, String path) {
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
            fileCursor = this.model.getActivity().getContentResolver().query(
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
            this.model.setPhotoFile(photoFile);
        }

        if(resultCode == Activity.RESULT_OK &&
                this.model.getPhotoFile() != null &&
                this.model.getPhotoFile().exists()){
            Log.d(TAG, "storeFileFromFileChooser: Photo exists");
            storePhotoFile("file");
        }
    }

    private void storePhotoFile(String directory){
        Log.d(TAG, "storePhotoFile: Photo exists");
        Storage storage = new Storage(this.model.getActivity());
        storage.copyFileToInternalStorage(this.model.getPhotoFile(), directory, null);

        NotesheetFacade nf = new NotesheetFacade(this.model.getActivity());
        nf.insertNotesheet(null, directory + File.separator + this.model.getPhotoFile().getName());
    }

    public void dismissDialog(){
        this.model.getListener().dismissDialog();
    }

    public List<Notesheet> getNotesheets(@Nullable Directory parent){
        NotesheetFacade nf = new NotesheetFacade(model.getActivity());
        DirectoryFacade df = new DirectoryFacade(model.getActivity());

        Directory dir = parent == null ? df.getRoot() : parent;

        return nf.getNotesheets(dir);
        //return df.getChildren(dir);
    }

    public void openNotesheet(Notesheet notesheet) {
        ServerManager.getInstance().openNotesheet(notesheet);
    }

    public DirectoryFacade getDirectoryFacade(){
        return new DirectoryFacade(model.getActivity());
    }

//    private void sendFileData(byte[] buffer){
//        BluetoothPackage data = new BluetoothPackage();
//        data.setFlag(Flag.FILEDATA);
//        data.setContent(buffer);
//        //Server.getInstance().sendPackage(data);
//    }

    public void tryStartBluetoothServer() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter != null){
            if(adapter.isEnabled() == false){

                IntentFilter bsStateChangedFilter =
                        new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                model.getActivity()
                        .registerReceiver(mBtStateChangedReceiver, bsStateChangedFilter);

                Toast
                    .makeText(model.getActivity(), R.string.ask_for_bluetooth, Toast.LENGTH_LONG)
                    .show();
            }else{
                ServerManager.getInstance().startServer();
            }
        }
    }

    public void unregisterBluetoothFilter(){
        try {
            model.getActivity().unregisterReceiver(mBtStateChangedReceiver);
        }catch(Exception e){
            //No catch routine because exception is unnecessary
        }
    }
}
