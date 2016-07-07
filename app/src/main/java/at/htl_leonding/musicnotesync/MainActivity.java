package at.htl_leonding.musicnotesync;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import at.htl_leonding.musicnotesync.helper.intent.CameraIntentHelper;
import at.htl_leonding.musicnotesync.helper.permission.PermissionHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private File mPhotoFile = null;
    private Dialog mSelectFormatDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Dialog selectTypeDialog = new Dialog(MainActivity.this);
//                selectTypeDialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//                selectTypeDialog.setContentView(R.layout.select_format_dialog);
//                selectTypeDialog.setTitle(getString(R.string.select_format));
//                selectTypeDialog.show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.select_format_dialog, null);

                Button btnTakePicture = (Button) dialogView.findViewById(R.id.btnTakePicture);
                btnTakePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean granted = PermissionHelper.verifyCameraPermissions(MainActivity.this);
                        if(granted == true) {
                            dispatchCameraIntent();
                        }
                    }
                });

                builder.setView(dialogView);
                builder.setTitle(getString(R.string.select_format));


                mSelectFormatDialog = builder.create();
                mSelectFormatDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CameraIntentHelper.REQUEST_CODE:
                Log.d(TAG, "onActivityResult: Camera intent closed");
                if(resultCode == RESULT_OK && mPhotoFile != null && mPhotoFile.exists()){
                    Log.d(TAG, "onActivityResult: Photo exists");
                }
                break;
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PermissionHelper.CAMERA_REQUEST_CODE:
                dispatchCameraIntent();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if(mSelectFormatDialog != null){
            mSelectFormatDialog.dismiss();
            mSelectFormatDialog = null;
        }

        super.onResume();
    }

    private void dispatchCameraIntent(){
        try {
            mPhotoFile = CameraIntentHelper.dispatchTakePictureIntent(MainActivity.this);
        } catch (IOException e) {
            Log.e(TAG, "onClick: " + e.getMessage());
        }
    }
}
