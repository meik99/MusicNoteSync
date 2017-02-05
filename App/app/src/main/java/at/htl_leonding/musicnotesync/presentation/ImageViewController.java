package at.htl_leonding.musicnotesync.presentation;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.io.File;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.socket.Server;

import static at.htl_leonding.musicnotesync.presentation.ImageViewActivity.EXTRA_PATH_NAME;

/**
 * Created by michael on 1/30/17.
 */

public class ImageViewController implements Server.ServerListener, View.OnClickListener, View.OnDragListener, View.OnScrollChangeListener {
    private static final String TAG = ImageViewController.class.getSimpleName();
    private ImageViewActivity mActivity;
    private ImageViewModel mModel;

    public ImageViewController(ImageViewActivity activity){
        mActivity = activity;
        mModel = new ImageViewModel(this);
        mModel.setImageView(
                (TouchImageView) mActivity.findViewById(R.id.noteSheetView));

        getFilenameFromIntent();
        getFileAsBitmap();
        setupImageView();

        mModel.getImageView().setOnClickListener(this);
        mModel.getImageView().setOnDragListener(this);
        mModel.getImageView().setOnScrollChangeListener(this);
        mModel.getImageView().invalidate();

        Server.getInstance().addListener(this);
    }

    private void setupImageView(){
        Bitmap bitmap = mModel.getBitmap();
        TouchImageView imageView = mModel.getImageView();

        if(bitmap != null){
            double scale = (double)bitmap.getWidth() / bitmap.getHeight();
            long newHeight = 0;
            DisplayMetrics metrics = new DisplayMetrics();

            mActivity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(metrics);

            newHeight = Math.round(metrics.widthPixels / scale);
            imageView.setImageBitmap(
                    Bitmap
                            .createScaledBitmap(bitmap,
                                    (int)metrics.widthPixels,
                                    (int)newHeight,
                                    false)
            );
        }else{
            imageView.setImageBitmap(bitmap);
        }
    }

    private void getFileAsBitmap() {
        String path = mActivity
                .getApplicationContext()
                .getFilesDir()
                .getPath() +
                File.separator +
                mModel.getFilename();
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        Log.i(TAG, "onCreate: " + path);

        mModel.setBitmap(bitmap);
    }

    private void getFilenameFromIntent(){
        if(mActivity.getIntent() != null){
            if(mActivity.getIntent().hasExtra(
                    ImageViewActivity.EXTRA_PATH_NAME)){
                String filename = mActivity.getIntent()
                        .getStringExtra(EXTRA_PATH_NAME);

                mModel.setFilename(filename);
            }
        }
    }

    @Override
    public void onServerDeviceConnected(BluetoothSocket socket) {

    }

    @Override
    public void onServerMessageReceived(BluetoothSocket socket, String message) {

    }

    @Override
    public void onServerDeviceDisconnected(BluetoothSocket socket) {

    }

    @Override
    public void onClick(View v) {
        System.out.printf("clicked");
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        System.out.printf("dragged");
        return false;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        System.out.printf("scrolled");
    }
}
