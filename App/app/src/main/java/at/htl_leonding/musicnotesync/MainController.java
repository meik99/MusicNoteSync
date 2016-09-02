package at.htl_leonding.musicnotesync;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileDescriptor;
import java.util.List;

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
        nf.insertNotesheet(null, this.model.getPhotoFile().getName());
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

    }
}
