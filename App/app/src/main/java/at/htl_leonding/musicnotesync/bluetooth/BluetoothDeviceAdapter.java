package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.blt.BltRepository;

/**
 * Created by michael on 13.09.16.
 */
public class BluetoothDeviceAdapter
        extends ArrayAdapter<BluetoothDevice>
        implements BltRepository.BltRepositoryListener{
    private final BluetoothController mBluetoothController;
    private List<BluetoothDevice> devices;

    public BluetoothDeviceAdapter(Context context, BluetoothController bluetoothController) {
        super(context, android.R.layout.simple_list_item_1);

        mBluetoothController = bluetoothController;
        BltRepository.getInstance().addRepositoryListener(this);
        devices = BltRepository.getInstance().getFoundDevices();
    }

    @Override
    protected void finalize() throws Throwable {
        BltRepository.getInstance().removeRepositoryListener(this);
        super.finalize();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return devices.get(position);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View result = layoutInflater.inflate(R.layout.bluetooth_device_list_item, parent, false);
        TextView txtDeviceName = (TextView) result.findViewById(R.id.txtDeviceName);
        CheckBox chkDeviceSelect = (CheckBox) result.findViewById(R.id.chkBluetoothDeviceSelect);

        txtDeviceName.setText(getItem(position).getName());
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheckbox(v);
                toggleDeviceSelected(position);
            }
        });
        chkDeviceSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDeviceSelected(position);
            }
        });

        return result;
    }

    private void toggleCheckbox(View v) {
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.chkBluetoothDeviceSelect);
        checkBox.toggle();
    }

    private void toggleDeviceSelected(int position){
        mBluetoothController.toggleDevice(
                devices.get(position)
        );
    }

    @Override
    public void onDeviceAdded() {
        devices = BltRepository.getInstance().getFoundDevices();
        this.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        devices = BltRepository.getInstance().getFoundDevices();
        this.notifyDataSetChanged();
    }
}
