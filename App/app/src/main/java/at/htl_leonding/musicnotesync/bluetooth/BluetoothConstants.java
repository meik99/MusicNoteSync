package at.htl_leonding.musicnotesync.bluetooth;

import java.util.Random;
import java.util.UUID;

/**
 * Created by michael on 22.08.16.
 */
public class BluetoothConstants {
    private BluetoothConstants(){

    }

    public static final UUID CONNECTION_UUID =
            UUID.fromString("42614F77-77E5-441A-90E3-5521182F4E6D");
    public static int BUFFER_SIZE = 1028;
}
