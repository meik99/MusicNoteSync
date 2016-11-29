package at.htl_leonding.musicnotesync.management.move.listener;

import android.view.View;

import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.management.move.MoveController;

/**
 * Created by michael on 11/29/16.
 */

public class NotesheetClickListener implements View.OnClickListener {
    private MoveController mMoveController;

    public NotesheetClickListener(MoveController moveController){
        mMoveController = moveController;
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();

        if(object != null){
            if(object instanceof Directory) {
                mMoveController.openDirectory((Directory) object);
            }
        }
    }
}
