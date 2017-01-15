package at.htl_leonding.musicnotesync.server.listener;

import org.apache.http.HttpEntity;

/**
 * Created by michael on 1/15/17.
 */

public interface DownloadListener {
    void downloadBegin();
    void downloadFinished(boolean success, HttpEntity entity, String filename);
}
