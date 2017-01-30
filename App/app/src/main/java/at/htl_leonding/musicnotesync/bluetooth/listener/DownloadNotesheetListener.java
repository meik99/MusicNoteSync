package at.htl_leonding.musicnotesync.bluetooth.listener;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.server.listener.DownloadListener;

/**
 * Created by michael on 1/16/17.
 */

public class DownloadNotesheetListener implements DownloadListener {
    private static final String TAG = DownloadNotesheetListener.class.getSimpleName();
    private final Context mContext;
    private LinkedList<NotesheetFacade.NotesheetDbListener> mListener;

    public DownloadNotesheetListener(Context context) {
        mContext = context;
        mListener = new LinkedList<>();
    }

    public void addAllNotesheetDbListener(
            at.htl_leonding.musicnotesync.db.facade.NotesheetFacade.NotesheetDbListener listener){
        if(listener != null){
            mListener.add(listener);
        }
    }

    public void removeNotesheetDbListener(
            at.htl_leonding.musicnotesync.db.facade.NotesheetFacade.NotesheetDbListener listener){
        if(listener != null){
            mListener.remove(listener);
        }
    }


    public void addAllNotesheetDbListener(List<NotesheetFacade.NotesheetDbListener> listener) {
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
                                 HttpEntity entity,
                                 String filename,
                                 String uuid,
                                 AndroidHttpClient client) {
        if(success == false){
            Log.i(TAG, "downloadFinished: download not successful");
        }else {
            NotesheetFacade notesheetFacade
                    = new NotesheetFacade(
                    mContext
            );
            filename = filename.replace("\n\r", "").trim();
            try {
                addAllListenerToFacade(notesheetFacade, mListener);
                notesheetFacade.insertFromInputStream(
                        entity.getContent(),
                        "bluetooth",
                        filename,
                        uuid
                );
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addAllListenerToFacade(NotesheetFacade notesheetFacade,
                                        List<NotesheetFacade.NotesheetDbListener> listeners){
        for (NotesheetFacade.NotesheetDbListener listener: listeners) {
            if(listener != null){
                notesheetFacade.addListener(listener);
            }
        }
    }
}
