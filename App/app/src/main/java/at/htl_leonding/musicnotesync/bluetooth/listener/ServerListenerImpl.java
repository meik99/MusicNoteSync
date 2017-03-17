package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.socket.Server;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;
import at.htl_leonding.musicnotesync.presentation.BluetoothNotesheetOpener;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;

/**
 * Created by michael on 1/15/17.
 */

public class ServerListenerImpl implements Server.ServerListener{
    private static final String TAG = ServerListenerImpl.class.getSimpleName();
    private final Activity mActivity;

    public ServerListenerImpl(Activity activity){
        mActivity = activity;
    }

    @Override
    public void onServerDeviceConnected(BluetoothSocket socket) {
        Log.i(TAG, "onServerDeviceConnected: " + socket.getRemoteDevice().getName() + " connected");
    }

    @Override
    public void onServerMessageReceived(BluetoothSocket socket, String message) {
        Log.i(TAG, "onServerMessageReceived: " + message);
        String[] data = message.split(";");

        if(data[data.length-1].endsWith("\n\r")){
            data[data.length-1] = data[data.length-1].substring(0, data[data.length-1].length()-2);
        }

        if(data.length > 0){
            if(data[0].equals(Notesheet.class.getSimpleName())){
                if(data.length == 3){
                    String uuid = data[1];
                    String name = data[2];
                    NotesheetContext notesheetFacade
                            = new NotesheetContext(
                            mActivity);
                    NotesheetFacade facade = new NotesheetFacade();
                    Notesheet notesheet = notesheetFacade.findByUUID(uuid);
                    DownloadNotesheetListener downloadNotesheetListener =
                            new DownloadNotesheetListener(mActivity);

                    if(notesheet == null) {
                        facade.downloadNotesheet(uuid, name, downloadNotesheetListener);
                    }else{
                    }
                }else if(data.length == 2){
                    String uuid = data[1];
                    BluetoothNotesheetOpener opener = new BluetoothNotesheetOpener(mActivity);
                    opener.openNotesheet(socket, uuid);
                }
            }
            else if(data[0].equals(Server.GET)){
                String uuid = data[1];

            }
        }
    }

    @Override
    public void onServerDeviceDisconnected(BluetoothSocket socket) {
        Log.i(TAG, "onServerDeviceDisconnected: " + socket.getRemoteDevice().getName() +
                " disconnected");
    }
}
