package at.htl_leonding.musicnotesync.server.facade;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGetHC4;

import java.io.IOException;

import at.htl_leonding.musicnotesync.server.listener.DownloadListener;

/**
 * Created by michael on 1/15/17.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private final String mUuid;
    private final String mFilename;
    private final String mServerUrl;
    private final DownloadListener[] mDownloadListener;
    private HttpEntity loadedEntity;


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
        AndroidHttpClient client = AndroidHttpClient.newInstance("user");
        HttpGetHC4 getNotesheetRequest = new HttpGetHC4(mServerUrl + "/" + mUuid);
        try {
            HttpResponse response = client.execute(getNotesheetRequest);
            loadedEntity = response.getEntity();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
            listener.downloadFinished(success, loadedEntity, mFilename);
        }
    }
}
