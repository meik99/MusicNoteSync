package at.htl_leonding.musicnotesync.blt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import at.htl_leonding.musicnotesync.blt.receiver.BluetoothState;

/**
 * Created by michael on 3/13/17.
 */

public class BltServerThread implements Runnable {
    private final BltService mBltService;

    BltServerThread(BltService bltService){
        mBltService = bltService;
    }

    @Override
    public void run() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        try {
            BluetoothServerSocket serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                    adapter.getName(),
                    BltConstants.CONNECTION_UUID
            );

            while (mBltService.getBluetoothState() == BluetoothState.ON) {
                BluetoothSocket clientSocket = serverSocket.accept();

                if(clientSocket != null) {
                    BltRepository.getInstance().addConnection(clientSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
