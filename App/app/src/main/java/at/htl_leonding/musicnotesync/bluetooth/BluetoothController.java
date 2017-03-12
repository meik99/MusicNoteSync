package at.htl_leonding.musicnotesync.bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.listener.BluetoothOpenNotesheetClickListener;
import at.htl_leonding.musicnotesync.bluetooth.listener.BluetoothSendNotesheetClickListener;
import at.htl_leonding.musicnotesync.bluetooth.listener.ServerListenerImpl;
import at.htl_leonding.musicnotesync.bluetooth.socket.Client;
import at.htl_leonding.musicnotesync.bluetooth.socket.Server;
import at.htl_leonding.musicnotesync.bluetooth.listener.NotesheetUploadListener;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.presentation.ImageViewActivity;
import at.htl_leonding.musicnotesync.presentation.TouchImageView;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothController{
    private final static String TAG = BluetoothController.class.getSimpleName();

    public static final int SET_DISCOVERABLE_REQUEST_CODE = 100;

    private final BluetoothActivity mBluetoothActivity;
    private final BluetoothModel mModel;
    private ProgressDialog loadingDialog;

    /**
     * Creates an instance of BluetoothController.
     * @param bluetoothActivity A BluetoothActivity-Instance the BluetoothController-Instance is
     *                          assigned to.
     */
    public BluetoothController(BluetoothActivity bluetoothActivity){
        mBluetoothActivity = bluetoothActivity;
        mModel = new BluetoothModel(mBluetoothActivity);

        mModel.setDeviceAdapter(new BluetoothDeviceAdapter(mBluetoothActivity, this));
        mModel.setServerListener(new ServerListenerImpl(bluetoothActivity));
    }

    public BluetoothDeviceAdapter getDeviceAdapter(){
        return mModel.getDeviceAdapter();
    }

    /**
     * Checks if device has bluetooth functionality. If functionality is given,
     * obtains a BluetoothAdapter and enables Bluetooth. <u><b>Does not</b></u> ask for
     * permissions.
     * Asks for enabling discoverable and then starts discovering devices.
     * @return true if Bluetooth has been enabled, else false
     */
    public boolean enableBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter.isEnabled() == true){
            if(bluetoothAdapter.getScanMode() !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                enableDiscoverable(mBluetoothActivity);
            }
        }
        return true;
    }

    public static void enableDiscoverable(Activity activity){
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
        activity.startActivityForResult(discoverableIntent, SET_DISCOVERABLE_REQUEST_CODE);
    }

    public void showToast(@StringRes int stringRes){
        Toast.makeText(mBluetoothActivity, stringRes, Toast.LENGTH_LONG)
                .show();
    }


    public View.OnClickListener getOnClickListener() {
        Intent activityIntent = mBluetoothActivity.getIntent();
        long entityId = activityIntent.getLongExtra(BluetoothActivity.ENTITY_ID, -1);

        if(activityIntent.hasExtra(BluetoothActivity.OPERATION) && entityId != -1){
            long operationId = activityIntent.getLongExtra(BluetoothActivity.OPERATION, -1);

            if(operationId == BluetoothActivity.SEND_NOTESHEET){

                return new BluetoothSendNotesheetClickListener(
                        this,
                        mModel.getNotesheetFacade().findById(entityId));
            }else if(operationId == BluetoothActivity.OPEN_NOTESHEET){
                return new BluetoothOpenNotesheetClickListener(
                        this,
                        mModel.getNotesheetFacade().findById(entityId)
                );
            }
        }

        Toast.makeText(mBluetoothActivity, R.string.error_creating_activity, Toast.LENGTH_SHORT)
                .show();
        mBluetoothActivity.finish();

        return null;
    }


    public int getActionButtonText() {
        Intent activityIntent = mBluetoothActivity.getIntent();

        if(activityIntent.hasExtra(BluetoothActivity.OPERATION)){
            long operationId = activityIntent.getLongExtra(BluetoothActivity.OPERATION, -1);

            if(operationId == BluetoothActivity.SEND_NOTESHEET){
                return R.string.send_notesheet;
            }
            else if(operationId == BluetoothActivity.OPEN_NOTESHEET){
                return R.string.open_notesheet;
            }
        }
        return R.string.error_creating_activity;
    }

    public void toggleDevice(BluetoothDevice bluetoothDevice) {
        if(mModel.selectedDeviceListContainsDevice(bluetoothDevice)){
            mModel.removeSelectedBluetoothDevice(bluetoothDevice);
        }else{
            mModel.addSelectedBluetoothDevice(bluetoothDevice);
        }
    }

    public void sendNotesheetMetadata(final Notesheet notesheet) {
        final List<BluetoothDevice> devices = mModel.getSelectedBluetoothDevices();
        Client client = new Client();
        boolean success = false;

        for (final BluetoothDevice bluetoothDevice : devices){
            AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    sendMetadataToDevice(notesheet, bluetoothDevice);
                    return null;
                }
            };
            asyncTask.execute();
        }
    }

    private void sendMetadataToDevice(Notesheet notesheet, BluetoothDevice device){
        Client client = new Client();
        client.connect(device);
        //TODO: add success Response
        boolean success = client.sendMessage(notesheet.getMetadata());


        Snackbar snackbar = null;
        if(success == true) {
            snackbar = Snackbar
                    .make(
                            mBluetoothActivity.findViewById(R.id.bluetoothActivityLayout),
                            R.string.transfer_successful,
                            Snackbar.LENGTH_SHORT);
        }else{
            snackbar = Snackbar
                    .make(
                            mBluetoothActivity.findViewById(R.id.bluetoothActivityLayout),
                            R.string.transfer_unsuccessful,
                            Snackbar.LENGTH_SHORT);
        }
        final Snackbar finalSackbar = snackbar;
        mBluetoothActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finalSackbar.show();
            }
        });
    }

    public void showLoadingAnimation() {
        loadingDialog = new ProgressDialog(mBluetoothActivity);
        loadingDialog.setCancelable(false);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(mBluetoothActivity.getString(R.string.uploading_Notesheet));
        loadingDialog.show();
    }

    public void stopLoadingAnimation() {
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
    }

    public void sendNotesheet(Notesheet notesheet){
        NotesheetUploadListener notesheetUploadListener =
                new NotesheetUploadListener(this);
        NotesheetFacade notesheetFacade = new NotesheetFacade();

        notesheetFacade.sendNotesheet(mBluetoothActivity, notesheet, notesheetUploadListener);
    }

    public void showSnackbar(@StringRes int stringRes) {
        Snackbar snackbar = Snackbar
                .make(
                        mBluetoothActivity.findViewById(R.id.bluetoothActivityLayout),
                        stringRes,
                        Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    public void openNotesheet(Notesheet notesheet) {
        StringBuilder builder = new StringBuilder();
        String[] adresses = new String[mModel.getSelectedBluetoothDevices().size()];
        List<BluetoothDevice> selectedBluetoothDevices = mModel.getSelectedBluetoothDevices();

        for (int i = 0; i < selectedBluetoothDevices.size(); i++) {
            BluetoothDevice clientDevice = selectedBluetoothDevices.get(i);
            builder.append(Notesheet.class.getSimpleName())
                    .append(";")
                    .append(notesheet.getUUID());

            AsyncTask<Object, Void, Void> task = new AsyncTask<Object, Void, Void>() {
                @Override
                protected Void doInBackground(Object... params) {
                    Client client = new Client();
                    client.connect((BluetoothDevice) params[0]);
                    client.sendMessage((String) params[1]);

                    return null;
                }

            };
            task.execute(clientDevice, builder.toString());


            builder = new StringBuilder();

            adresses[i] = selectedBluetoothDevices.get(i).getAddress();
        }

        Intent notesheetView = new Intent(mBluetoothActivity, ImageViewActivity.class);
        notesheetView.putExtra(ImageViewActivity.EXTRA_PATH_NAME, notesheet.getPath());
        notesheetView.putExtra(ImageViewActivity.EXTRA_CLIENTS, adresses);
        mBluetoothActivity.startActivity(notesheetView);
    }
}
