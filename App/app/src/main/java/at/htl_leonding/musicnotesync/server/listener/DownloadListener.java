package at.htl_leonding.musicnotesync.server.listener;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpEntity;

/**
 * Created by michael on 1/15/17.
 */

public interface DownloadListener {
    void downloadBegin();
    void downloadFinished(boolean success,
                          byte[] data,
                          String filename,
                          String uuid,
                          AndroidHttpClient client);
}
