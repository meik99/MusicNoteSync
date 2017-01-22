package at.htl_leonding.musicnotesync.bluetooth.socket;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by mrynkiewicz on 1/11/17.
 */

public class BluetoothExecutor {
    public static final Executor BLUETOOTH_EXECUTOR =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

    private BluetoothExecutor(){}

}
