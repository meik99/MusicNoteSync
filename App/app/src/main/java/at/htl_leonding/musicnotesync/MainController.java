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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.bluetooth.connection.BluetoothPackage;
import at.htl_leonding.musicnotesync.bluetooth.connection.Flag;
import at.htl_leonding.musicnotesync.bluetooth.connection.Server;
import at.htl_leonding.musicnotesync.bluetooth.deprecated.communication.BluetoothCommunicator;
import at.htl_leonding.musicnotesync.bluetooth.deprecated.server.BluetoothServer;
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
    }

    public void openNotesheet(Notesheet ns) {
        if(Server.getInstance().isRunning() == true){
            BluetoothPackage file = new BluetoothPackage();
            ByteBuffer bb = ByteBuffer.allocate(BluetoothConstants.BUFFER_CONTENT_SIZE);
            File noteFile = ns.getFile();

            file.setFlag(Flag.FILE);
            bb.put(ns.getUUID().getBytes());
            bb.put(";".getBytes());
            bb.put(ns.getName().getBytes());
            file.setContent(bb.array());

            Server.getInstance().sendPackage(file);

            try {
                if(noteFile != null) {
                    BufferedInputStream br = new BufferedInputStream(new FileInputStream(noteFile));
                    byte[] buffer = new byte[BluetoothConstants.BUFFER_CONTENT_SIZE];

                    while (br.read(buffer) > -1) {
                        BluetoothPackage data = new BluetoothPackage();
                        data.setFlag(Flag.FILEDATA);
                        data.setContent(buffer);
                        Server.getInstance().sendPackage(data);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tryStartBluetoothServer() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter != null){
            if(adapter.isEnabled() == false){
                BroadcastReceiver btStateChanged = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int bltState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                        if(bltState == BluetoothAdapter.STATE_ON) {
                            Server.getInstance().startServer();
                        }else{
                            Server.getInstance().stopServer();
                        }
                    }
                };
                IntentFilter bsStateChangedFilter =
                        new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                model.getActivity()
                        .registerReceiver(btStateChanged, bsStateChangedFilter);

                Toast
                    .makeText(model.getActivity(), R.string.ask_for_bluetooth, Toast.LENGTH_LONG)
                    .show();
            }else{
                Server.getInstance().startServer();
            }
        }
    }
}
