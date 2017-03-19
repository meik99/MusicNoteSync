package at.htl_leonding.musicnotesync;

import java.util.List;

import at.htl_leonding.musicnotesync.blt.BltConnection;
import at.htl_leonding.musicnotesync.blt.BltRepository;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;

/**
 * Created by michael on 3/18/17.
 */

public abstract class BaseController implements
        BltRepository.BltRepositoryListener,
        BltRepository.BltConnectListener {

    public BaseController(){
        BltRepository.getInstance().addRepositoryListener(this);
        BltRepository.getInstance().addConnectListener(this);
    }

    @Override
    public void onDeviceAdded() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onMessageReceived(String message) {
        String[] data = message.split(";");
        if(data[0].equals(Notesheet.class.getSimpleName())){

        }
    }

    @Override
    public void onConnected(BltConnection connection) {

    }

    @Override
    public void onBulkConnected(List<BltConnection> connections) {

    }
}
