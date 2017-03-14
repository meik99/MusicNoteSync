package at.htl_leonding.musicnotesync.blt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;
import at.htl_leonding.musicnotesync.io.WatchableBase64InputStream;

/**
 * Created by michael on 3/11/17.
 */

public class BltRepository {
    public interface BltRepositoryListener{
        void onDeviceAdded();
        void onRefresh();
    }
    public interface BltConnectListener{
        void onConnected(BltConnection connection);
        void onBulkConnected(List<BltConnection> connections);
    }

    public List<BluetoothDevice> getFoundDevices() {
        return foundDevices;
    }

    private static final int MAX_SKIPS = 10;

    public class BltConnection {

        public BluetoothDevice device;
        public WatchableBase64InputStream inputStream;
        public Base64OutputStream outputStream;
    }
    private static BltRepository instance = null;

    private List<BltConnection> connections;

    private List<BluetoothDevice> foundDevices;
    private List<BltRepositoryListener> repositoryListeners;

    private List<BltConnectListener> connectListener = new ArrayList<>();
    private Queue<String> messageQueue;
    private Thread messageSender;
    private BltRepository(){
        connections = new ArrayList<>();
        foundDevices = new ArrayList<>();
        repositoryListeners = new ArrayList<>();
        messageQueue = new ArrayDeque<>();
    }

    public void refresh() {
        foundDevices.clear();

        for (BltRepositoryListener listener :
                repositoryListeners) {
            listener.onRefresh();
        }
    }

    public void addRepositoryListener(BltRepositoryListener listener){
        if(listener != null && repositoryListeners.contains(listener) == false){
            repositoryListeners.add(listener);
        }
    }

    public void removeRepositoryListener(BltRepositoryListener listener){
        repositoryListeners.remove(listener);
    }

    void addFoundDevice(BluetoothDevice device){
        boolean isKnown = false;

        for (BluetoothDevice bluetoothDevice:
             foundDevices) {
            if(bluetoothDevice.getAddress().equals(device.getAddress())){
                isKnown = true;
            }
        }

        if(isKnown == false){
            foundDevices.add(device);
            for (BltRepositoryListener listener :
                    repositoryListeners) {
                listener.onDeviceAdded();
            }
        }
    }

    void addConnection(BluetoothSocket socket){
        BluetoothDevice device = socket.getRemoteDevice();
        boolean isKnown = false;

        for (BltConnection connection :
                connections) {
            if(connection.device.getAddress().equals(device.getAddress())){
                isKnown = true;
            }
        }

        if(isKnown == false){
            BltConnection connection = new BltConnection();
            connection.device = socket.getRemoteDevice();
            try {
                connection.inputStream =
                        new WatchableBase64InputStream(socket.getInputStream(), Base64.DEFAULT);
                connection.outputStream =
                        new Base64OutputStream(socket.getOutputStream(), Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect(BluetoothDevice device){
        AsyncTask<BluetoothDevice, Void, Void> task = new AsyncTask<BluetoothDevice, Void, Void>() {
            @Override
            protected Void doInBackground(BluetoothDevice... params) {
                BltConnection connection = connectSyncron(params[0]);

                for(BltConnectListener listener :
                        BltRepository.getInstance().connectListener){
                    listener.onConnected(connection);
                }

                return null;
            }
        };
        task.execute(device);
    }

    private BltConnection connectSyncron(BluetoothDevice device){
        boolean isKnown = false;

        for (BltConnection connection :
                connections) {
            if (connection.device.getAddress().equals(device.getAddress())){
                isKnown = true;
                return connection;
            }
        }

        if(isKnown == false) {
            try {
                BluetoothSocket socket =
                        device.createRfcommSocketToServiceRecord(BltConstants.CONNECTION_UUID);
                BltConnection connection = new BltConnection();

                connection.device = device;
                connection.inputStream =
                        new WatchableBase64InputStream(socket.getInputStream(), Base64.DEFAULT);
                connection.outputStream =
                        new Base64OutputStream(socket.getOutputStream(), Base64.DEFAULT);

                BltRepository.getInstance().connections.add(connection);
                return connection;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void bulkConnect(List<BluetoothDevice> bluetoothDevices){
        BluetoothDevice[] devices = new BluetoothDevice[bluetoothDevices.size()];
        devices = bluetoothDevices.toArray(devices);

        AsyncTask<BluetoothDevice, Void, Void> task = new AsyncTask<BluetoothDevice, Void, Void>() {
            @Override
            protected Void doInBackground(BluetoothDevice... params) {
                List<BltConnection> connections = new LinkedList<>();

                for(BluetoothDevice device: params){
                    BltConnection connection = connectSyncron(device);
                    connections.add(connection);
                }


                for(BltConnectListener listener :
                        BltRepository.getInstance().connectListener){
                    listener.onBulkConnected(connections);
                }

                return null;
            }
        };
        task.execute(devices);
    }

    public static BltRepository getInstance(){
        if(instance == null){
            instance = new BltRepository();
            instance.refresh();
        }
        return instance;
    }

    public void sendMessage(final String message){
        messageQueue.add(message);
        if(messageSender == null || messageSender.isAlive() == false){
            messageSender = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            int skipCount = 0;
                            while(messageQueue.isEmpty() == false){
                                String currentMessage = messageQueue.remove();
                                boolean sendMessage = false;

                                if(messageQueue.size() >= 1){
                                    if(skipCount >= MAX_SKIPS ||
                                        currentMessage.split(";")[0].equals(
                                            messageQueue.peek().split(";")[0]
                                    ) == false){
                                        skipCount = 0;
                                        sendMessage = true;
                                    }else{
                                        skipCount++;
                                        sendMessage = false;
                                    }
                                }

                                if(sendMessage == true) {
                                    for (BltConnection connection :
                                            connections) {
                                        try {
                                            connection.outputStream.write(
                                                    currentMessage.getBytes()
                                            );
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
            );
            messageSender.start();
        }
    }


    public void addBltConnectListenerListener(BltConnectListener listener){
        if(listener != null && connectListener.contains(listener) == false){
            connectListener.add(listener);
        }
    }

    public void removeBltConnectListenerListener(BltConnectListener listener){
        connectListener.remove(listener);
    }
}
