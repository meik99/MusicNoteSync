package at.htl_leonding.musicnotesync.bluetooth.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 21.09.16.
 */

public class PackageSender extends Thread {
    private static PackageSender instance = null;

    private static final String TAG = PackageSender.class.getSimpleName();
    private List<BluetoothPackage> buffer;
    private List<BluetoothSocket> clients;
    private boolean isRunning = false;

    private PackageSender(){
        buffer = new LinkedList<>();
        clients = new LinkedList<>();
        this.start();
    }

    public static PackageSender getInstance(){
        if(instance == null){
            instance = new PackageSender();
        }
        return instance;
    }

    public void addMessage(BluetoothPackage message) {
        buffer.add(message);
    }

    @Override
    public void run() {
        super.run();
        isRunning = true;

        while(isRunning == true){
            if(buffer != null && buffer.size() > 0){
                BluetoothPackage pack = buffer.remove(0);
                List<BluetoothSocket> tmpClients = clients;

                for(BluetoothSocket client : tmpClients){
                    boolean dirty;
                    int tryCount = 0;

                    do{
                        dirty = false;
                        try {
                            OutputStream outputStream = client.getOutputStream();
                            InputStream inputStream = client.getInputStream();
//                            List<Integer> byteBuffer = new LinkedList<>();
                            int b;
                            byte[] answerStream = new byte[BluetoothConstants.BUFFER_MAX_SIZE];
                            int answerCount = 0;

                            outputStream.write(pack.toByteArray());

//                            while(inputStream.read() == -1 &&
//                                    answerCount++ < BluetoothConstants.TRY_MAX){
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            while((b = inputStream.read()) != -1){
//                                byteBuffer.add(b);
//                            }
//
//                            received = new Byte[byteBuffer.size()];
//                            received = byteBuffer.toArray(received);

                            b = inputStream.read(answerStream);

                            if(b < 0){
                                Log.i(TAG, "run: length was " + b);
                            }
                            else {
                                BluetoothPackage answer = BluetoothPackage.fromByteArray(answerStream);
                                if (answer.getFlag() != Flag.POSITIVE) {
                                    dirty = true;
                                    Log.i(TAG, "run: answer not positive");
                                }
                            }

                        } catch (IOException e) {
                            dirty = true;
                            Log.i(TAG, "run: " + e.getMessage());
                        }
                    }
                    while(dirty == true && tryCount++ < BluetoothConstants.TRY_MAX);
                }
            }
            else{
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        isRunning = false;
        this.clients = null;
    }

    public void setClients(List<BluetoothSocket> clients) {
        for(BluetoothSocket socket : clients){
            if(socket != null){
                BluetoothDevice device = socket.getRemoteDevice();

                if(device != null){
                    if(this.clients.size() <= 0){
                        this.clients.add(socket);
                    }else {
                        String address = device.getAddress();

                        for (BluetoothSocket pSocket : this.clients) {
                            BluetoothDevice pDevice = pSocket.getRemoteDevice();
                            String pAddress = pDevice.getAddress();

                            if (pAddress.equals(address) == false) {
                                this.clients.add(socket);
                            }
                        }
                    }
                }
            }
        }
    }
}
