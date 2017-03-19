package at.htl_leonding.musicnotesync.blt.decorator;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.blt.BltConstants;
import at.htl_leonding.musicnotesync.blt.listener.InputStreamListener;

/**
 * Created by michael on 3/12/17.
 */

public class WatchableBase64InputStream extends Base64InputStream {
    private static final String TAG = WatchableBase64InputStream.class.getSimpleName();
    private List<InputStreamListener> listeners;
    private boolean isWatching = false;
    private static final int MEGABYTE = 1024000;
    /**
     * An InputStream that performs Base64 decoding on the data read
     * from the wrapped stream.
     *
     * @param in    the InputStream to read the source data from
     * @param flags bit flags for controlling the decoder; see the
     *              constants in {@link Base64}
     */
    public WatchableBase64InputStream(InputStream in, int flags) {
        super(in, flags);

        listeners = new ArrayList<>();

        startWatching();
    }

    private void startWatching() {
        isWatching = true;

        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int tryCount = 0;
                        while(isWatching){
                            StringBuilder builder = new StringBuilder();
                            byte[] buffer = new byte[MEGABYTE];
                            int read = -1;

                            if(WatchableBase64InputStream.this != null &&
                                    WatchableBase64InputStream.this.in != null) {
                                try {
                                    while((read = WatchableBase64InputStream.this.read(buffer)) > -1) {
                                        Log.d(TAG, "Read: " + new String(buffer, 0, read));
                                        builder.append(new String(buffer, 0, read));
                                    }
                                    Log.d(TAG, "run: exit loop");
                                } catch (IOException | NullPointerException e) {
                                    e.printStackTrace();
                                    if(tryCount++ >= BltConstants.TRY_MAX){
                                        isWatching = false;
                                    }
                                }

                                if(builder.length() > 0) {
                                    for (InputStreamListener listener :
                                            listeners) {
                                        listener.onMessageReceived(builder.toString());
                                    }
                                }
                            }
                        }
                    }
                }
        );
        t.start();
    }

    public void stop(){
        isWatching = false;
    }

    public void addListener(InputStreamListener listener) {
        if(listener != null) {
            if(listeners.contains(listener) == false){
                listeners.add(listener);
            }
        }
    }

    public void removeListener(InputStreamListener listener){
        listeners.remove(listener);
    }
}
