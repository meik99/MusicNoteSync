package at.htl_leonding.musicnotesync;

import java.io.File;

import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;

/**
 * Created by michael on 11.08.16.
 */
public class MainModel {
    private MainActivity activity;
    private FabOnClickListener fabOnClickListener;
    private File photoFile;

    public MainModel(){

    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity mActivity) {
        this.activity = mActivity;
    }

    public FabOnClickListener getListener() {
        return fabOnClickListener;
    }

    public void setListener(FabOnClickListener mListener) {
        this.fabOnClickListener = mListener;
    }
}
