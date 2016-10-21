package at.htl_leonding.musicnotesync.bluetooth.connection.server;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import at.htl_leonding.musicnotesync.bluetooth.connection.Connection;
import at.htl_leonding.musicnotesync.bluetooth.connection.ConnectionManager;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 19.10.16.
 */

public class ServerManager implements Server.ServerListener{
    private static final String TAG = ServerManager.class.getSimpleName();

    private static ServerManager instance;
    private static boolean started = false;

    private Server mServer;
    private Executor mExecutor;
    private ConnectionManager mConnectionManager;

    private ServerManager() {
        mServer = new Server();
        mServer.addListener(this);
        mConnectionManager = new ConnectionManager();
    }

    public static ServerManager getInstance(){
        if(instance == null){
            instance = new ServerManager();
        }
        return instance;
    }

    public void startServer(){
        if(started == false){
            mExecutor = new ScheduledThreadPoolExecutor(1);
            mExecutor.execute(mServer);
            started = true;
        }
    }

    @Override
    public void onConnect(BluetoothSocket socket) {
        if(socket != null) {
            mConnectionManager.addConnection(
                    new Connection(socket)
            );
        }
    }

    public void openNotesheet(Notesheet notesheet) {
        sendNotesheetMetadata(notesheet);
        try{
            sendNotesheetData(notesheet);
        } catch (IOException e) {
            Log.i(TAG, "openNotesheet: " + e.getMessage());
        }

    }

    private void sendNotesheetData(Notesheet notesheet) throws IOException {
        File file = notesheet.getFile();

        if(file == null || file.exists() == false){
            String exceptionMessage = TAG + " File: " + file.getName() + " doesn't exists";
            throw new IOException(exceptionMessage);
        }

        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;

        while((line = reader.readLine()) != null){
            Log.d(TAG, "sendNotesheetData: Read line: \n\t" + line);
            content.append(line);
        }

        mConnectionManager.sendMessage(content.toString());
    }

    private void sendNotesheetMetadata(Notesheet notesheet){
        String name = notesheet.getName();
        String uuid = notesheet.getUUID();

        mConnectionManager.sendMessage(name);
        mConnectionManager.sendMessage(uuid);
    }
}
