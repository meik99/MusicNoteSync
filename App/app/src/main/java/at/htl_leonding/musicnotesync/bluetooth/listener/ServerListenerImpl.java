package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Base64InputStream;
import android.util.Log;

import org.apache.http.HttpEntity;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import at.htl_leonding.musicnotesync.bluetooth.socket.Server;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.server.listener.DownloadListener;

/**
 * Created by michael on 1/15/17.
 */

public class ServerListenerImpl implements Server.ServerListener{
    private static final String TAG = ServerListenerImpl.class.getSimpleName();
    private final Context mContext;


    public ServerListenerImpl(Context context){
        mContext = context;
    }

    @Override
    public void onServerDeviceConnected(BluetoothSocket socket) {

    }

    @Override
    public void onServerMessageReceived(BluetoothSocket socket, String message) {
        Log.i(TAG, "onServerMessageReceived: " + message);
        String[] data = message.split(";");

        if(data.length > 0){
            if(data[0].equals(Notesheet.class.getSimpleName())){
                if(data.length >= 3){
                    String uuid = data[1];
                    String name = data[2];
                    NotesheetFacade facade = new NotesheetFacade();
                    facade.downloadNotesheet(uuid, name, new DownloadListener() {
                        @Override
                        public void downloadBegin() {
                            Log.i(TAG, "downloadBegin: starting download");
                        }

                        @Override
                        public void downloadFinished(boolean success, HttpEntity entity, String filename) {
                            if(success == false){
                                Log.i(TAG, "downloadFinished: download not successful");
                            }else {
                                at.htl_leonding.musicnotesync.db.facade.NotesheetFacade notesheetFacade
                                        = new at.htl_leonding.musicnotesync.db.facade.NotesheetFacade(
                                        mContext
                                );
                                filename = filename.replace("\n\r", "").trim();
                                try {
                                    Notesheet inserted = notesheetFacade.insertFromInputStream(
                                            entity.getContent(),
                                            "bluetooth",
                                            filename
                                    ).get();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onServerDeviceDisconnected(BluetoothSocket socket) {

    }
}
