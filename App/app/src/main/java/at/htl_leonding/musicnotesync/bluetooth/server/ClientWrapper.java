package at.htl_leonding.musicnotesync.bluetooth.server;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.bluetooth.communication.Command;
import at.htl_leonding.musicnotesync.bluetooth.communication.Operation;

/**
 * Created by michael on 05.09.16.
 */
public class ClientWrapper extends Thread{
    private static final String TAG = ClientWrapper.class.getSimpleName();

    private BluetoothSocket mClientSocket;

    private Operation mLastOperationSent;
    private Operation mLastOperationReceived;

    private Command mLastCommandSent;
    private Command mLastCommandReceived;

    private Exception lastException;

    private List<OperationProcessor> mOperationProcessors;

    private boolean mRunning;

    public ClientWrapper(BluetoothSocket clientSocket){
        if(clientSocket == null){
            throw new IllegalArgumentException("Client socket cannot be null");
        }

        mClientSocket = clientSocket;
        mOperationProcessors = new ArrayList<>();
        mRunning = false;
    }

    public void addOperationProcessor(OperationProcessor operationProcessor){
        if(operationProcessor != null){
            mOperationProcessors.add(operationProcessor);
        }
    }

    public void removeOperationProcessor(OperationProcessor operationProcessor){
        if(operationProcessor != null & mOperationProcessors.contains(operationProcessor)){
            mOperationProcessors.remove(operationProcessor);
        }
    }

    @Override
    public void run() {
        mRunning = true;

        while(mRunning == true){
            try {
                InputStream is = mClientSocket.getInputStream();
                byte[] buffer;
                int read;

            } catch (IOException e) {
                lastException = e;
                Log.i(TAG, "run: " + e.getMessage());
                fireOnException();
            }
        }
    }

    private void fireOnException(){
        if(mOperationProcessors != null && mOperationProcessors.size() > 0){
            for(OperationProcessor op : mOperationProcessors){
                op.onError(this);
            }
        }
    }

    private void fireOnOperationReceived(){
        if(mOperationProcessors != null && mOperationProcessors.size() > 0) {
            for (OperationProcessor op : mOperationProcessors) {
                op.onOperationReceived(this);
            }
        }
    }

    private void fireOnOperationFinished(){
        if(mOperationProcessors != null && mOperationProcessors.size() > 0){
            for(OperationProcessor op : mOperationProcessors) {
                op.onOperationFinished(this);
            }
        }
    }

    public void cancel(){
        mRunning = false;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public Operation getLastOperationSent() {

        return mLastOperationSent;
    }

    public Operation getLastOperationReceived() {
        return mLastOperationReceived;
    }

    public Command getLastCommandSent() {
        return mLastCommandSent;
    }

    public Command getLastCommandReceived() {
        return mLastCommandReceived;
    }

    public Exception getLastException() {
        return lastException;
    }
}
