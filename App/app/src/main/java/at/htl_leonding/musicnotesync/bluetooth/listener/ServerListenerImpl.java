package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.socket.Server;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.presentation.BluetoothNotesheetOpener;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;

/**
 * Created by michael on 1/15/17.
 */

public class ServerListenerImpl implements Server.ServerListener{
    private static final String TAG = ServerListenerImpl.class.getSimpleName();
    private final Activity mActivity;

    private List<at.htl_leonding.musicnotesync.db.facade.NotesheetFacade.NotesheetDbListener>
            mListener;

    public ServerListenerImpl(Activity activity){
        mActivity = activity;
        mListener = new LinkedList<>();
    }

    public void addNotesheetDbListener(
            at.htl_leonding.musicnotesync.db.facade.NotesheetFacade.NotesheetDbListener listener){
        if(listener != null){
            mListener.add(listener);
        }
    }

    public void removeNotesheetDbListener(
            at.htl_leonding.musicnotesync.db.facade.NotesheetFacade.NotesheetDbListener listener){
        if(listener != null){
            mListener.remove(listener);
        }
    }

    @Override
    public void onServerDeviceConnected(BluetoothSocket socket) {

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
                    at.htl_leonding.musicnotesync.db.facade.NotesheetFacade notesheetFacade
                            = new at.htl_leonding.musicnotesync.db.facade.NotesheetFacade(
                            mActivity);
                    NotesheetFacade facade = new NotesheetFacade();
                    Notesheet notesheet = notesheetFacade.findByUUID(uuid);
                    DownloadNotesheetListener downloadNotesheetListener =
                            new DownloadNotesheetListener(mActivity);

                    downloadNotesheetListener.addAllNotesheetDbListener(mListener);

                    if(notesheet == null) {
                        facade.downloadNotesheet(uuid, name, downloadNotesheetListener);
                    }else{
                        for (at.htl_leonding.musicnotesync.db.facade.
                                NotesheetFacade.NotesheetDbListener listener :
                                mListener) {
                            listener.onNotesheetInserted(notesheet);
                        }
                    }
                }else if(data.length == 2){
                    String uuid = data[1];
                    BluetoothNotesheetOpener opener = new BluetoothNotesheetOpener(mActivity);
                    opener.openNotesheet(uuid);
                }
            }
        }
    }

    @Override
    public void onServerDeviceDisconnected(BluetoothSocket socket) {

    }
}
