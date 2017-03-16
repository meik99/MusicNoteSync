package at.htl_leonding.musicnotesync.server.listener;

import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;

/**
 * Created by michael on 1/15/17.
 */

public interface UploadListener {
    void onUploadBegin();
    void onUploadFinished(boolean success, Notesheet notesheet);
}
