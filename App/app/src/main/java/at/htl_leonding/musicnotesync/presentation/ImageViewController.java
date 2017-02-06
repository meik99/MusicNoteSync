package at.htl_leonding.musicnotesync.presentation;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.socket.Client;
import at.htl_leonding.musicnotesync.bluetooth.socket.Server;

import static at.htl_leonding.musicnotesync.presentation.ImageViewActivity.EXTRA_PATH_NAME;

/**
 * Created by michael on 1/30/17.
 */

public class ImageViewController implements Server.ServerListener, ZoomListener {
    public static final String COMMAND_ZOOM = "ZOOM";

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

        mModel.getImageView().invalidate();
        mModel.getImageView().addZoomListener(this);
        mModel.setBluetoothSockets(new ArrayList<BluetoothSocket>());

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
        if(socket != null && mModel.getBluetoothSockets().contains(socket) == false){
            mModel.getBluetoothSockets().add(socket);
        }
    }

    @Override
    public void onServerMessageReceived(BluetoothSocket socket, String message) {
        if(message != null){
            String[] data = message.split(";");

            if(data.length > 0){
                if (data[0].equals(COMMAND_ZOOM)){
                    ZoomHandler handler = new ZoomHandler(data);

                    if(handler.isValid()){
                        mModel.getImageView().setZoom(
                                handler.getScale(),
                                handler.getFocusX(),
                                handler.getFocusY(),
                                handler.getScaleType()
                        );
                    }
                }
            }
        }
    }

    @Override
    public void onServerDeviceDisconnected(BluetoothSocket socket) {
        mModel.getBluetoothSockets().remove(socket);
    }

    @Override
    public void onZoomBegin(TouchImageView view) {
//        Log.i(TAG, "onZoom: start zoom");
    }

    @Override
    public void onZoom(TouchImageView view) {
        RectF rectangel = view.getZoomedRect();

        float scale = view.getCurrentZoom();
        float focusX = rectangel.centerX();
        float focusY = rectangel.centerY();
        ImageView.ScaleType scaleType = view.getScaleType();

        StringBuilder message = new StringBuilder();
        message.append(COMMAND_ZOOM)
                .append(";")
                .append(scale)
                .append(";")
                .append(focusX)
                .append(";")
                .append(focusY)
                .append(";")
                .append(scaleType.name());
        String messageString = message.toString();

        for (BluetoothSocket socket :
                mModel.getBluetoothSockets()) {
            Client client = new Client();
            client.connect(socket.getRemoteDevice());
            client.sendMessage(messageString);
        }
    }

    @Override
    public void onZoomEnd(TouchImageView view) {
//        Log.i(TAG, "onZoom: end zoom");
    }
}
