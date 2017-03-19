package at.htl_leonding.musicnotesync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;

import java.util.List;

import at.htl_leonding.musicnotesync.blt.BltRepository;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.infrastructure.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.infrastructure.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.io.Storage;
import at.htl_leonding.musicnotesync.presentation.ImageViewActivity;

/**
 * Created by michael on 3/18/17.
 */

public abstract class BaseController implements
        BltRepository.BltRepositoryListener,
        BltRepository.BltConnectListener {

    protected final Context context;
    protected final NotesheetFacade notesheetFacade;
    protected final DirectoryFacade directoryFacade;

    protected final BaseModel baseModel;

    public BaseController(Context context, BaseModel model){
        BltRepository.getInstance().addRepositoryListener(this);
        BltRepository.getInstance().addConnectListener(this);

        notesheetFacade = new NotesheetFacade(context);
        directoryFacade = new DirectoryFacade(context);
        this.context = context;
        baseModel = model == null ? new BaseModel() : model;
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
            if(data.length == 3){
                notesheetFacade.downloadNotesheet(data[1], data[2], baseModel.getActiveDirectory());
            }
            else if(data.length == 2){
                Notesheet notesheet = notesheetFacade.findByUUID(data[1]);

                if(notesheet == null){
                    notesheet = notesheetFacade.downloadNotesheet(
                            data[1],
                            data[1] + ".jpg",
                            baseModel.getActiveDirectory());
                }

                openNotesheet(notesheet);
            }
        }
    }

    protected void openNotesheet(Notesheet notesheet){
        Intent openNotesheet = new Intent(context, ImageViewActivity.class);
        openNotesheet.putExtra(
                ImageViewActivity.EXTRA_CLIENTS, baseModel.getCurrentConnections());
        openNotesheet.putExtra(ImageViewActivity.EXTRA_PATH_NAME, notesheet.getPath());
        context.startActivity(openNotesheet);
    }

    @Override
    public void onConnected(BltRepository.BltConnection connection) {

    }

    @Override
    public void onBulkConnected(List<BltRepository.BltConnection> connections) {
        String[] addresses = new String[connections.size()];

        for (int i = 0; i < connections.size(); i++){
            addresses[i] = connections.get(i).device.getAddress();
        }

        baseModel.setCurrentConnections(addresses);
    }

}
