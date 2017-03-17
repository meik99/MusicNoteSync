package at.htl_leonding.musicnotesync.blt.decorator;

import android.util.Base64;
import android.util.Base64InputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.blt.listener.InputStreamListener;

/**
 * Created by michael on 3/12/17.
 */

public class WatchableBase64InputStream extends Base64InputStream {
    private List<InputStreamListener> listeners;
    private boolean isWatching = false;
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
                        while(isWatching){
                            StringBuilder builder = new StringBuilder();
                            byte[] buffer = new byte[Integer.MAX_VALUE];
                            int read = -1;

                            try {
                                do{
                                    read = WatchableBase64InputStream.this.read(buffer);
                                    builder.append(new String(buffer, 0, read));
                                }while(read >= 0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            for (InputStreamListener listener :
                                    listeners) {
                                listener.onMessageReceived(builder.toString());
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
