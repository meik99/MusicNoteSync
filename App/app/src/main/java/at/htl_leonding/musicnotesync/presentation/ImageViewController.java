package at.htl_leonding.musicnotesync.presentation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.socket.Server;
import at.htl_leonding.musicnotesync.presentation.control.move.MoveHandler;
import at.htl_leonding.musicnotesync.presentation.control.move.TouchImageViewMoveListener;
import at.htl_leonding.musicnotesync.presentation.control.zoom.TouchImageViewZoomListener;
import at.htl_leonding.musicnotesync.presentation.control.zoom.ZoomHandler;

import static at.htl_leonding.musicnotesync.presentation.ImageViewActivity.EXTRA_PATH_NAME;

/**
 * Created by michael on 1/30/17.
 */

public class ImageViewController implements Server.ServerListener {
    public static final String MOVE = "move";
    public static final String ZOOM = "zoom";

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

        getBluetoothDevices();

        mModel.getImageView().invalidate();
        mModel.getImageView().addZoomListener(
                new TouchImageViewZoomListener(mActivity, mModel.getBluetoothDevices())
        );
        mModel.getImageView().setOnTouchImageViewListener(
                new TouchImageViewMoveListener(mActivity, mModel.getBluetoothDevices())
        );

        Server.getInstance().addListener(this);
    }

    private void getBluetoothDevices(){
        Intent intent = mActivity.getIntent();

        if(intent != null){
            if(intent.hasExtra(ImageViewActivity.EXTRA_CLIENTS)){
                String[] addresses = intent.getStringArrayExtra(ImageViewActivity.EXTRA_CLIENTS);

                for (String address : addresses) {
                    mModel.getBluetoothDevices().add
                            (
                                    BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
                            );
                }
            }
        }
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
        String[] data = message.split(";");
        if(data[0].equals(ZOOM)){
            final ZoomHandler handler = new ZoomHandler(data);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(handler.getScaleType() != null){
                        mModel.getImageView().setZoom(
                                handler.getScale(),
                                handler.getFocusX(),
                                handler.getFocusY(),
                                handler.getScaleType());
                    }else{
                        mModel.getImageView().setZoom(
                                handler.getScale(),
                                handler.getFocusX(),
                                handler.getFocusY()
                        );
                    }
                }
            });
        }else if(data[0].equals(MOVE)){
            final MoveHandler handler = new MoveHandler(data);
            if(handler.isValid()) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mModel.getImageView()
                                .setScrollPosition(
                                        handler.getX(),
                                        handler.getY()
                                );
                    }
                });
            }
        }
    }

    @Override
    public void onServerDeviceDisconnected(BluetoothSocket socket) {

    }
}
