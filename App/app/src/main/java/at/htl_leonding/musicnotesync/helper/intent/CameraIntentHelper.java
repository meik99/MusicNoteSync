package at.htl_leonding.musicnotesync.helper.intent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by michael on 07.07.16.
 */
public class CameraIntentHelper {
    private CameraIntentHelper(){}

    public static final int REQUEST_CODE = 1;

    /**
     * Creates a temporary file.
     * Can be used to store a picture taken by
     * a camera-intent
     * @param filename Filename of created file. If null, 'tmp_file' will be used
     * @param extension Extension for created file. If null, no extension will be added
     * @return Returns created file
     * @throws IOException Throws if File cannot be created for whatever reason
     */
    public static File createStorageFile(@Nullable String filename, @Nullable String extension) throws IOException {
        filename = filename == null ? "tmp_file" : filename;
        extension = extension == null ? "" : extension;

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                filename,
                extension,
                storageDir
        );
        Log.w(CameraIntentHelper.class.getSimpleName(), "Nicememe");
        return image;
    }

    /**
     * Creates a camera-intent and dispatches it. The intent stores the picture taken into
     * the given storage-file.
     * @param rootActivity The activity that calls the intent. Needed for PackageManager and
     *                     starting the intent with a result-code
     * @param storageFile Used for storing the picture taken.
     * @return Returns the storageFile given as argument for further procedures
     */
    public static File dispatchTakePictureIntent(@NonNull Activity rootActivity, @NonNull File storageFile){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Check if phone has a camera-app available.
        if(takePictureIntent.resolveActivity(rootActivity.getPackageManager()) != null){
             if(storageFile != null){
                 //Creates a path pointing to the storage file.
                 //Used by intent
                 Uri photoUri = Uri.fromFile(storageFile);
                 takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                 //Starts intent with result-code.
                 //Commented for later listener-implementation
                 rootActivity.startActivityForResult(takePictureIntent, REQUEST_CODE);
             }
        }

        return storageFile;
    }

    /**
     * Creates a camera-intent and dispatches it. The intent stores the picture taken into
     * a newly created storage-file.
     * @param rootActivity The activity that calls the intent. Needed for PackageManager and
     *                     starting the intent with a result-code
     * @return Returns the created file for further procedures
     * @throws IOException Throws if the storage-file cannot be created for whatever reason
     */
    public static File dispatchTakePictureIntent(@NonNull Activity rootActivity) throws IOException {
        //Creates a storage file and calls dispatch-method
        return CameraIntentHelper.dispatchTakePictureIntent(
                    rootActivity,
                    CameraIntentHelper.createStorageFile(null, ".jpg"));
    }


//    private static void askPermissions(Activity rootActivity){
//        String neededPermissions[] = {
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.CAMERA
//        };
//
//        boolean hasPermission = true;
//        for(String permission : neededPermissions){
//            if(rootActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
//
//            }
//        }
//    }
}
