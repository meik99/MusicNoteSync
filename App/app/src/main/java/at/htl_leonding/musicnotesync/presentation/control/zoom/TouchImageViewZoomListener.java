package at.htl_leonding.musicnotesync.presentation.control.zoom;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.util.List;

import at.htl_leonding.musicnotesync.blt.BltRepository;
import at.htl_leonding.musicnotesync.bluetooth.socket.Client;
import at.htl_leonding.musicnotesync.presentation.ImageViewController;
import at.htl_leonding.musicnotesync.presentation.TouchImageView;

/**
 * Created by michael on 2/15/17.
 */
public class TouchImageViewZoomListener implements ZoomListener {
    private final List<BluetoothDevice> mBluetoothDevices;
    private final Activity mActivity;

    public TouchImageViewZoomListener(Activity activity, List<BluetoothDevice> bluetoothDevices) {
        mBluetoothDevices = bluetoothDevices;
        mActivity = activity;
    }

    @Override
    public void onZoomBegin(TouchImageView view) {

    }

    @Override
    public void onZoom(final TouchImageView view) {

    }

    @Override
    public void onZoomEnd(final TouchImageView view) {
        final StringBuilder builder = new StringBuilder();

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

                BltRepository.getInstance().sendMessage(builder.toString());
//                AsyncTask task = new AsyncTask() {
//                    @Override
//                    protected Object doInBackground(Object[] params) {
//                        for (BluetoothDevice device :
//                                mBluetoothDevices) {
//                            Client client = new Client();
//                            client.connect(device);
//                            client.sendMessage(builder.toString());
//                        }
//
//                        return null;
//                    }
//                };
//                task.execute();
            }
        });

    }
}
