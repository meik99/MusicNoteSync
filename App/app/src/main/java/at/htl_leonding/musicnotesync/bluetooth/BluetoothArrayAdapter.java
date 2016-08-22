package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 22.08.16.
 */
public class BluetoothArrayAdapter extends ArrayAdapter implements BluetoothDeviceFoundListener{
    private List<BluetoothDevice> mDevice;
    private BluetoothController mController;
    private int mResource;

    public BluetoothArrayAdapter(Context context, int resource, BluetoothController controller) {
        super(context, resource);
        mDevice = new LinkedList<>();
        mController = controller;
        mResource = resource;
    }


    @Override
    public void deviceFound(BluetoothDevice device) {
        mDevice = mController.getDevices();
    }

    @Override
    public int getCount() {
        return mDevice.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevice.get(position);
    }

    @Override
    public int getPosition(Object item) {
        return mDevice.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) convertView.inflate(getContext(), mResource, parent);
        view.setText(mDevice.get(position).getName());

        return view;
    }
}
