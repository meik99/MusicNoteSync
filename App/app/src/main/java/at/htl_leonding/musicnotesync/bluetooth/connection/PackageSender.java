package at.htl_leonding.musicnotesync.bluetooth.connection;

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

    private static final String TAG = PackageSender.class.getSimpleName();
    private List<BluetoothPackage> buffer;
    private List<BluetoothSocket> clients;
    private boolean isRunning = false;

    public PackageSender(){
        buffer = new LinkedList<>();
        clients = null;
    }

    public void addMessage(BluetoothPackage message) {
        buffer.add(message);
    }

    @Override
    public void run() {
        super.run();
        isRunning = true;

        while(buffer != null && buffer.size() > 0){
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
                        List<Integer> byteBuffer = new LinkedList<>();
                        int b;
                        Byte[] received;

                        outputStream.write(pack.toByteArray());

                        while(inputStream.read() == -1){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                Log.i(TAG, "run: " + e.getMessage());
                            }
                        }

                        while((b = inputStream.read()) != -1){
                            byteBuffer.add(b);
                        }

                        received = new Byte[byteBuffer.size()];
                        received = byteBuffer.toArray(received);

                        BluetoothPackage answer = BluetoothPackage.fromByteArray(received);
                        if(answer.getFlag() != Flag.POSITIVE){
                            dirty = true;
                            Log.i(TAG, "run: answer not positive");
                        }

                    } catch (IOException e) {
                        dirty = true;
                        Log.i(TAG, "run: " + e.getMessage());
                    }
                }
                while(dirty == true && tryCount++ < BluetoothConstants.TRY_MAX);
            }
        }

        isRunning = false;
        this.clients = null;
        this.destroy();
    }

    public void send(List<BluetoothSocket> clients){
        boolean firstStart = false;
        if(this.clients == null){
            firstStart = true;
            this.clients = new LinkedList<>();
        }

        if(clients != null){
            this.clients = clients;
        }

        if(firstStart == true){
            this.start();
        }
    }
}
