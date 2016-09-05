package at.htl_leonding.musicnotesync.bluetooth.server;

/**
 * Created by michael on 05.09.16.
 */
public interface OperationProcessor {
    void onOperationReceived(ClientWrapper clientWrapper);
    void onOperationFinished(ClientWrapper clientWrapper);
    void onError(ClientWrapper clientWrapper);
}
