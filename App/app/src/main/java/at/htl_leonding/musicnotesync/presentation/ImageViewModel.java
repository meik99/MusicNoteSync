package at.htl_leonding.musicnotesync.presentation;

import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 1/30/17.
 */

public class ImageViewModel {
    private final ImageViewController mController;
    private String filename;
    private Bitmap bitmap;
    private TouchImageView imageView;
    private List<BluetoothDevice> bluetoothDevices;

    public ImageViewModel(ImageViewController imageViewController) {
        mController = imageViewController;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setImageView(TouchImageView imageView) {
        this.imageView = imageView;
    }

    public TouchImageView getImageView() {
        return imageView;
    }

    public List<BluetoothDevice> getBluetoothDevices() {
        if(bluetoothDevices == null){
            bluetoothDevices = new LinkedList<>();
        }

        return bluetoothDevices;
    }

    public void setBluetoothDevices(List<BluetoothDevice> bluetoothDevices) {
        this.bluetoothDevices = bluetoothDevices;
    }
}
