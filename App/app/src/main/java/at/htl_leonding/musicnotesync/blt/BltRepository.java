package at.htl_leonding.musicnotesync.blt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import at.htl_leonding.musicnotesync.blt.interfaces.MessageReceivedListener;
import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;
import at.htl_leonding.musicnotesync.io.InputStreamListener;
import at.htl_leonding.musicnotesync.io.WatchableBase64InputStream;

/**
 * Created by michael on 3/11/17.
 */

public class BltRepository {
    private static final int MAX_SKIPS = 10;

    private class BltConnection {
        public BluetoothDevice device;
        public WatchableBase64InputStream inputStream;
        public Base64OutputStream outputStream;
    }

    private static BltRepository instance = null;

    private List<BltConnection> connections;
    private List<BluetoothDevice> foundDevices;
    private Queue<String> messageQueue;
    private Thread messageSender;

    private BltRepository(){
        connections = new ArrayList<>();
        foundDevices = new ArrayList<>();
        messageQueue = new ArrayDeque<>();
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

    public static BltRepository getInstance(){
        if(instance == null){
            instance = new BltRepository();
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
}
