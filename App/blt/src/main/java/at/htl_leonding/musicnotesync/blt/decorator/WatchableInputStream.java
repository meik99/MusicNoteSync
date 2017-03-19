package at.htl_leonding.musicnotesync.blt.decorator;

import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.blt.BltConstants;
import at.htl_leonding.musicnotesync.blt.listener.InputStreamListener;

/**
 * Created by michael on 3/12/17.
 */

public class WatchableInputStream extends InputStream {
    private static final String TAG = WatchableInputStream.class.getSimpleName();
    private final InputStream in;
    private List<InputStreamListener> listeners;
    private boolean isWatching = false;
    private static final int MEGABYTE = 1024000;
    /**
     * An InputStream that performs Base64 decoding on the data read
     * from the wrapped stream.
     */
    public WatchableInputStream(InputStream in) {
        //super(in, flags);
        super();

        this.in = in;
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

                            if(in != null /*&&
                                    WatchableInputStream.this.in != null*/) {
                                boolean messageRead = false;
                                try {
                                    while(messageRead == false && (read = in.read(buffer)) > -1) {
//                                        buffer = Base64.encode(buffer, 0 , read, Base64.DEFAULT);
                                        buffer = Base64.decode(buffer, 0, read, Base64.DEFAULT);


                                        Log.d(TAG, "Read: " +
                                                new String(buffer, Charset.forName(BltConstants.CHARSET)));
                                        builder.append(
                                                new String(
                                                        buffer, Charset.forName(BltConstants.CHARSET))
                                        );

                                        if(builder.substring(builder.length()-2).equals("\r\n")){
                                            messageRead = true;
                                        }
                                    }
                                    Log.d(TAG, "run: exit loop");
                                } catch (IOException | NullPointerException e) {
                                    e.printStackTrace();
                                    if(tryCount++ >= BltConstants.TRY_MAX){
                                        isWatching = false;
                                    }
                                }

                                if(builder.length() > 0) {
                                    final List<InputStreamListener> tmpListener = listeners;
                                    for (InputStreamListener listener :
                                            tmpListener) {
                                        listener.onMessageReceived(
                                                builder.toString().replace("\r\n", ""));
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

    @Override
    public int read() throws IOException {
        return 0;
    }
}
