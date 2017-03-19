package at.htl_leonding.musicnotesync.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import at.htl_leonding.musicnotesync.BaseController;
import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.blt.BltRepository;
import at.htl_leonding.musicnotesync.bluetooth.listener.BluetoothOpenNotesheetClickListener;
import at.htl_leonding.musicnotesync.bluetooth.listener.BluetoothSendNotesheetClickListener;
import at.htl_leonding.musicnotesync.bluetooth.socket.Client;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.presentation.ImageViewActivity;

/**
 * Created by michael on 12.09.16.
 */
public class BluetoothController extends BaseController{
    private final static String TAG = BluetoothController.class.getSimpleName();

    public static final int SET_DISCOVERABLE_REQUEST_CODE = 100;
    private static final String SEND_METADATA = "metadata";

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
        mModel.setActiveNotesheet(notesheet);
        mModel.setBluetoothAction(SEND_METADATA);

        BltRepository.getInstance().addConnectListener(this);
        BltRepository.getInstance().bulkConnect(devices);
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
        sendNotesheetMetadata(notesheet);
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

    @Override
    public void onConnected(BltRepository.BltConnection connection) {

    }

    @Override
    public void onBulkConnected(List<BltRepository.BltConnection> connections) {
        BltRepository.getInstance().removeBltConnectListenerListener(this);

        if(mModel.getBluetoothAction() != null &&
                mModel.getBluetoothAction().equals(SEND_METADATA)) {
            BltRepository.getInstance().sendMessage(mModel.getActiveNotesheet().getMetadata());

//            Snackbar.make(mBluetoothActivity.findViewById(R.id.bluetoothActivityLayout),
//                    R.string.transfer_successful,
//                    Snackbar.LENGTH_SHORT).show();
        }
    }
}
