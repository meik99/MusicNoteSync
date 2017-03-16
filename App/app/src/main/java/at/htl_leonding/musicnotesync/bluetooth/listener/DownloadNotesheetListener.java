package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;
import at.htl_leonding.musicnotesync.server.listener.DownloadListener;

/**
 * Created by michael on 1/16/17.
 */

public class DownloadNotesheetListener implements DownloadListener {
    private static final String TAG = DownloadNotesheetListener.class.getSimpleName();
    private final Context mContext;
    private LinkedList<NotesheetContext.NotesheetDbListener> mListener;

    public DownloadNotesheetListener(Context context) {
        mContext = context;
        mListener = new LinkedList<>();
    }

    public void addAllNotesheetDbListener(
            NotesheetContext.NotesheetDbListener listener){
        if(listener != null){
            mListener.add(listener);
        }
    }

    public void removeNotesheetDbListener(
            NotesheetContext.NotesheetDbListener listener){
        if(listener != null){
            mListener.remove(listener);
        }
    }


    public void addAllNotesheetDbListener(List<NotesheetContext.NotesheetDbListener> listener) {
        if(listener != null && listener.contains(null) == false){
            mListener.addAll(listener);
        }
    }

    @Override
    public void downloadBegin() {
        Log.i(TAG, "downloadBegin: starting download");
    }

    @Override
    public void downloadFinished(boolean success,
                                 byte[] bytes,
                                 String filename,
                                 String uuid,
                                 AndroidHttpClient client) {
        if(success == false){
            Log.i(TAG, "downloadFinished: download not successful");
        }else {
            NotesheetContext notesheetFacade
                    = new NotesheetContext(
                    mContext
            );
            filename = filename.replace("\n\r", "").trim();
                addAllListenerToFacade(notesheetFacade, mListener);
                notesheetFacade.insertFromInputStream(
                        bytes,
                        "bluetooth",
                        filename,
                        uuid
                );
                client.close();

        }
    }

    private void addAllListenerToFacade(NotesheetContext notesheetFacade,
                                        List<NotesheetContext.NotesheetDbListener> listeners){
        for (NotesheetContext.NotesheetDbListener listener: listeners) {
            if(listener != null){
                notesheetFacade.addListener(listener);
            }
        }
    }
}
