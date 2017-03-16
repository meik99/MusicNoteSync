package at.htl_leonding.musicnotesync.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.bluetooth.socket.Client;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;

/**
 * Created by michael on 1/28/17.
 */

public class BluetoothNotesheetOpener {
    private final Activity mActivity;

    public BluetoothNotesheetOpener(Activity activity){

        mActivity = activity;
    }

    public void openNotesheet(BluetoothSocket socket, String uuid){
        NotesheetContext notesheetFacade = new NotesheetContext(mActivity);
        Notesheet notesheet = notesheetFacade.findByUUID(uuid);

        if(notesheet != null){
            Intent intent = new Intent(mActivity, ImageViewActivity.class);
            intent.putExtra(ImageViewActivity.EXTRA_PATH_NAME, notesheet.getPath());
            mActivity.startActivity(intent);
        }else{
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setTitle(R.string.notesheet_not_found);
            dialog.setMessage(R.string.notesheet_not_found_message);
            dialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });

            Client client = new Client();
            client.connect(socket.getRemoteDevice());
            client.sendMessage("GET;" + uuid);
        }
    }
}
