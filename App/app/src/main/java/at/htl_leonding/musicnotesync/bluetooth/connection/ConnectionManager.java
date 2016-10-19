package at.htl_leonding.musicnotesync.bluetooth.connection;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 19.10.16.
 */

public class ConnectionManager {
    private List<Connection> mConnections;

    public ConnectionManager(){
        mConnections = new LinkedList<>();
    }

    public void addConnection(Connection connection){
        if(mConnections.indexOf(connection) < 0){
            mConnections.add(connection);
        }
    }

    public void sendMessage(String message){
        for(Connection connection : mConnections){
            connection.sendData(message);
        }
    }
}
