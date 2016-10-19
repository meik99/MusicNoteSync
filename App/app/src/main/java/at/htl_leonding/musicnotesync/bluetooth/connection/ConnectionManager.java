package at.htl_leonding.musicnotesync.bluetooth.connection;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by michael on 19.10.16.
 */

public class ConnectionManager implements Connection.ConnectionListener {
    private List<Connection> mConnections;
    private Executor mExecutor;
    private int mCoreCount = Runtime.getRuntime().availableProcessors();

    public ConnectionManager(){
        mConnections = new LinkedList<>();
    }

    public void addConnection(Connection connection){
        if(mConnections.indexOf(connection) < 0){
            mExecutor = Executors.newScheduledThreadPool(mCoreCount);

            connection.addListener(this);
            mConnections.add(connection);
            mExecutor.execute(connection);
        }
    }

    public void sendMessage(String message){
        for(Connection connection : mConnections){
            connection.sendData(message);
        }
    }

    @Override
    public void onMessageReceived(Connection connection, String message) {

    }

    @Override
    public void onConnectionClosed(Connection connection) {
        mConnections.remove(connection);
        connection = null;
    }

    public void closeConnections() {
        for(Connection connection : mConnections){
            if(connection != null){
                connection.close();
                connection = null;
            }
        }
    }
}
