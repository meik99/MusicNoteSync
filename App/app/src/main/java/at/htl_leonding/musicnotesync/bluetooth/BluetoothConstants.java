package at.htl_leonding.musicnotesync.bluetooth;

import java.util.Random;
import java.util.UUID;
import java.util.jar.Manifest;

/**
 * Created by michael on 22.08.16.
 */
public class BluetoothConstants {
    private BluetoothConstants(){

    }

    public static final UUID CONNECTION_UUID =
            UUID.fromString("42614F77-77E5-441A-90E3-5521182F4E6D");
    public static int BUFFER_CONTENT_SIZE = 1024;
    public static int BUFFER_FLAG_SIZE = 4;
    public static int BUFFER_MAX_SIZE = BUFFER_CONTENT_SIZE + BUFFER_FLAG_SIZE;
    public static int TRY_MAX = 10;

    /**
     * Has value of -1. Use BUFFER_CONTENT_SIZE and BUFFER_FLAG_SIZE instead
     */
    @Deprecated
    public static int BUFFER_SIZE = -1;
    public static String[] PERMISSIONS = new String[]{
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
}
