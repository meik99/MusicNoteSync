package at.htl_leonding.musicnotesync.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

import at.htl_leonding.musicnotesync.MainController;
import at.htl_leonding.musicnotesync.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.DirectoryImpl;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetImpl;
import at.htl_leonding.musicnotesync.helper.EmergencyStorage;
import at.htl_leonding.musicnotesync.management.MoveActivity;
import at.htl_leonding.musicnotesync.request.RequestCode;

/**
 * Created by michael on 11/26/16.
 */
public class ManagementOptionsClickListener implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private View mActiveView = null;
    private MainController mMainController;

    public ManagementOptionsClickListener(MainController mainController){
        mMainController = mainController;
    }

    @Override
    public void onClick(View v) {
        mActiveView = v;

        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.inflate(R.menu.file_management);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final Object object = mActiveView.getTag();

        switch (item.getItemId()){
            case R.id.move_option:
                mMainController.startMoveObjectToDirectory(object);
            break;
            case R.id.delete_option:
                mMainController.deleteNotesheetObject(object);
            break;
            case R.id.rename_option:
                mMainController.startRenameNotesheetObject(object);
                break;

        }
        mMainController.refreshNotesheetArrayAdapter();
        return false;
    }
}
