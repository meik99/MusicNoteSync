package at.htl_leonding.musicnotesync;

import android.view.View;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 02.09.16.
 */
public class NotesheetClickListener implements View.OnClickListener {
    private MainController mController;

    public NotesheetClickListener(MainController controller) {
        mController = controller;
    }

    @Override
    public void onClick(View view) {
        if(view.getTag() != null) {
            Notesheet ns = null;
            ns = (Notesheet) view.getTag();
            mController.openNotesheet(ns);
        }
    }
}
