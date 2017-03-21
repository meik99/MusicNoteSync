package at.htl_leonding.musicnotesync.mainactivity.listener;

import android.view.View;

import at.htl_leonding.musicnotesync.mainactivity.MainController;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;

/**
 * Created by michael on 02.09.16.
 */
public class NotesheetClickListener implements View.OnClickListener {
    private MainController mMainController;

    public NotesheetClickListener(MainController controller) {
        mMainController = controller;
    }

    @Override
    public void onClick(View itemView) {
        if(itemView.getTag() != null && !itemView.getTag().equals("Dir")) {
            Object object = (Object) itemView.getTag();

            if (object instanceof Notesheet) {
                mMainController.syncOpenNotesheet((Notesheet)object);
            }
            else{
                mMainController.openDirectory((Directory)object);
            }
        }
    }


}
