package at.htl_leonding.musicnotesync.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.connection.Client;
import at.htl_leonding.musicnotesync.bluetooth.connection.Server;

/**
 * Created by michael on 13.09.16.
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    private final List<BluetoothDevice> dataSet;

    public BluetoothDeviceAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);

        dataSet = new LinkedList<>();
    }

    public void setDataSet(List<BluetoothDevice> newDataSet){
        if(newDataSet != null){
            dataSet.clear();
            dataSet.addAll(newDataSet);
            notifyDataSetChanged();
        }
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View result = layoutInflater.inflate(R.layout.bluetooth_device_list_item, parent, false);
        TextView txtDeviceName = (TextView) result.findViewById(R.id.txtDeviceName);

        txtDeviceName.setText(getItem(position).getName());
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Server.getInstance().isConnected() == false) {
                    boolean connected = Client.getInstance().connect(getItem(position));
                    if (connected == true) {
                        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                        if (adapter != null) {
                            adapter.cancelDiscovery();
                        }
                        Toast
                                .makeText(
                                        v.getContext(),
                                        R.string.bluetooth_successful_connect,
                                        Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast
                                .makeText(
                                        v.getContext(),
                                        R.string.bluetooth_connect_failure,
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                }else{
                    Toast
                            .makeText(
                                    v.getContext(),
                                    R.string.bluetooth_device_is_server,
                                    Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        return result;
    }
}
