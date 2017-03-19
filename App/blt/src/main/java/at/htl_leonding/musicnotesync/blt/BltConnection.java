package at.htl_leonding.musicnotesync.blt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.OutputStream;

import at.htl_leonding.musicnotesync.blt.decorator.WatchableInputStream;

/**
 * Created by hanne on 19.03.2017.
 */

public class BltConnection {
    public BluetoothDevice device;
    public WatchableInputStream inputStream;
    public OutputStream outputStream;
    public BluetoothSocket socket;
}
