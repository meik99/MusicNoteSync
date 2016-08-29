package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.client.BluetoothEntryClickListener;

/**
 * Created by michael on 22.08.16.
 */
public class BluetoothArrayAdapter extends ArrayAdapter<BluetoothDevice>
        implements BluetoothDeviceFoundListener{
    private List<BluetoothDevice> mDevice;
    private BluetoothController mController;
    private int mResource;

    public BluetoothArrayAdapter(Context context, int resource, BluetoothController controller) {
        super(context, resource);
        mDevice = new LinkedList<>();
        mController = controller;
    }


    @Override
    public void deviceFound(BluetoothDevice device) {
        mDevice = mController.getDevices();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDevice.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return mDevice.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        BluetoothDevice device = mDevice.get(position);

        if(v == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.bluetooth_device_list_item, parent, false);
        }

        if(device != null){
            TextView deviceName = (TextView) v.findViewById(R.id.txtDeviceName);
            deviceName.setText(device.getName());
        }

        if(v != null){
            v.setTag(device);
            v.setOnClickListener(new BluetoothEntryClickListener(this.mController));
        }

        return v;
    }




}
