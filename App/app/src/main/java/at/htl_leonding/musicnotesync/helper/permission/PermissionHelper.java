package at.htl_leonding.musicnotesync.helper.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;

/**
 * Created by michael on 07.07.16.
 */
public class PermissionHelper {
    private PermissionHelper(){}

    public static final int STANDARD_REQUEST_CODE = 2;
    public static final int CAMERA_REQUEST_CODE = 3;


    public static boolean verifyPermissions(@NonNull Activity activity, String[] permissions){
        if(permissions == null)
            permissions = new String[0];

        boolean permissionsGranted = true;

        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED){
                permissionsGranted = false;
            }
        }

        if(permissionsGranted == false){
            ActivityCompat.requestPermissions(activity, permissions, STANDARD_REQUEST_CODE);
        }

        return permissionsGranted;
    }


    public static boolean verifyCameraPermissions(@NonNull Activity activity){
        return verifyPermissions(
                activity,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }
        );
    }
}
