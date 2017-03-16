package at.htl_leonding.musicnotesync.server.facade;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;


import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.server.listener.DownloadListener;

/**
 * Created by michael on 1/15/17.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, Boolean> {
    public static final String USER_AGENT = "";

    private final String mUuid;
    private final String mFilename;
    private final String mServerUrl;
    private final DownloadListener[] mDownloadListener;
    private HttpEntity mLoadedEntity;
    private AndroidHttpClient mClient;
    private byte[] receivedBytes;


    public DownloadAsyncTask(String serverUrl,
                             String uuid,
                             String filename,
                             DownloadListener... downloadListeners){
        mUuid = uuid;
        mFilename = filename;
        mServerUrl = serverUrl;
        mDownloadListener = downloadListeners;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
//        mClient = AndroidHttpClient.newInstance(null);
//        HttpGetHC4 getNotesheetRequest = new HttpGetHC4(mServerUrl + "/" + mUuid);
        boolean success = false;
//        try {
//            mLoadedEntity = mClient.execute(getNotesheetRequest).getEntity();
//
//            InputStream stream = mLoadedEntity.getContent();
//
//
//            byte[] bytes = new byte[(int) mLoadedEntity.getContentLength()];
//            int read = -1;
//            int offset = 0;
//
//            while((read = stream.read(bytes, offset, bytes.length)) > -1){
//                offset += read;
//            }
//
//            receivedBytes = bytes.clone();
//
//            success = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return success;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        for (DownloadListener listener:
                mDownloadListener) {
            listener.downloadBegin();
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        for (DownloadListener listener:
                mDownloadListener) {
            listener.downloadFinished(success, receivedBytes, mFilename, mUuid, mClient);
        }
    }
}
