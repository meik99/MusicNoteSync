package at.htl_leonding.musicnotesync.mainactivity.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.helper.intent.CameraIntentHelper;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

/**
 * Created by michael on 10.08.16.
 */
public class FabOnClickListener implements View.OnClickListener{
    public static final int SELECT_FILE_REQUEST_CODE = 4;

    private static final String TAG = FabOnClickListener.class.getSimpleName();

    private final Activity activity;

    private File mPhotoFile;
    private Dialog mSelectFormatDialog;

    public FabOnClickListener(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        final Context context = view.getContext();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_format_dialog, null);

        Button btnTakePicture = (Button) dialogView.findViewById(R.id.btnTakePicture);
        Button btnSelectFile = (Button) dialogView.findViewById(R.id.btnSelectFile);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean granted = PermissionHelper.verifyCameraPermissions(activity);
                if(granted == true) {
                    dispatchCameraIntent();
                }
            }
        });

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean granted = PermissionHelper.verifySelectFilePermissions(activity);
                if(granted == true){
                    dispatchSelectFileIntent();
                }
            }
        });

        builder.setView(dialogView);
        builder.setTitle(context.getString(R.string.select_format));


        mSelectFormatDialog = builder.create();
        mSelectFormatDialog.show();
    }

    private void dispatchSelectFileIntent() {
        String[] mimeTypes = new String[]{"image/*"};
        Intent selectFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectFileIntent.setType("*/*");
        selectFileIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        activity.startActivityForResult(selectFileIntent, SELECT_FILE_REQUEST_CODE);
    }


    private void dispatchCameraIntent(){
        try {
            mPhotoFile = CameraIntentHelper.dispatchTakePictureIntent(activity);
        } catch (IOException e) {
            Log.e(TAG, "onClick: " + e.getMessage());
        }
    }

    public File getPhotoFile(){
        return mPhotoFile;
    }

    public void dismissDialog(){
        if(mSelectFormatDialog != null){
            mSelectFormatDialog.dismiss();
            mSelectFormatDialog = null;
        }
    }
}
