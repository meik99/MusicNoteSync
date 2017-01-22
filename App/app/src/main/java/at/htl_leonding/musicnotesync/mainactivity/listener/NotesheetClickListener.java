package at.htl_leonding.musicnotesync.mainactivity.listener;

import android.content.Intent;
import android.view.View;

import at.htl_leonding.musicnotesync.presentation.ImageViewActivity;
import at.htl_leonding.musicnotesync.MainController;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;

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
                mMainController.openNotesheet((Notesheet)object);
            }
            else{
                mMainController.openDirectory((Directory)object);
            }
        }
    }


}
