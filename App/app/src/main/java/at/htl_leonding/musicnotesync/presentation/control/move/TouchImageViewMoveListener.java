package at.htl_leonding.musicnotesync.presentation.control.move;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.socket.Client;
import at.htl_leonding.musicnotesync.presentation.ImageViewActivity;
import at.htl_leonding.musicnotesync.presentation.ImageViewController;
import at.htl_leonding.musicnotesync.presentation.TouchImageView;

/**
 * Created by michael on 3/9/17.
 */
public class TouchImageViewMoveListener implements TouchImageView.OnTouchImageViewListener, View.OnTouchListener {
    private static final String TAG = TouchImageViewMoveListener.class.getSimpleName();
    private final List<BluetoothDevice> mBluetoothDevices;
    private final ImageViewActivity mActivity;

    public TouchImageViewMoveListener(ImageViewActivity activity, List<BluetoothDevice> bluetoothDevices) {
        mBluetoothDevices = bluetoothDevices;
        mActivity = activity;
    }

    @Override
    public void onMove() {
        final StringBuilder builder = new StringBuilder();
        final TouchImageView view = (TouchImageView) mActivity.findViewById(R.id.noteSheetView);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.append(ImageViewController.ZOOM)
                        .append(";")
                        .append(view.getCurrentZoom())
                        .append(";")
                        .append(view.getZoomedRect().centerX())
                        .append(";")
                        .append(view.getZoomedRect().centerY())
                        .append(";")
                        .append(view.getScaleType().name());

                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        for (BluetoothDevice device :
                                mBluetoothDevices) {
                            Client client = new Client();
                            client.connect(device);
                            client.sendMessage(builder.toString());
                        }

                        return null;
                    }
                };
                task.execute();
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        onMove();
        return true;
    }
}
