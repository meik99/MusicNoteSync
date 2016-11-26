package at.htl_leonding.musicnotesync.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

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
    private NotesheetArrayAdapter mAdapter = null;
    private Activity mActivity = null;

    public ManagementOptionsClickListener(NotesheetArrayAdapter adapter, Activity activity){
        mAdapter = adapter;
        mActivity = activity;
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
                Intent intent = new Intent(mActivity, MoveActivity.class);

                if(object instanceof Directory){
                    Directory directory = (Directory)object;

                    EmergencyStorage.id = directory.getId();
                    mActivity.startActivityForResult(
                            intent, RequestCode.MOVE_DIRECTORY_REQUEST_CODE);
                }
                else if(object instanceof  Notesheet){
                    Notesheet notesheet = (Notesheet) object;

                    EmergencyStorage.id = notesheet.getId();
                    mActivity.startActivityForResult(intent, RequestCode.MOVE_NOTESHEET_REQUEST_CODE);
                }
                break;
            case R.id.delete_option:
                if(object instanceof Directory){
                    DirectoryFacade df = new DirectoryFacade(mActiveView.getContext());
                    df.delete((Directory) object);
                }
                else if(object instanceof Notesheet){
                    NotesheetFacade nf = new NotesheetFacade(mActiveView.getContext());
                    nf.delete((Notesheet)object);
                }
                break;
            case R.id.rename_option:
                AlertDialog.Builder builder = new AlertDialog.Builder(mActiveView.getContext());
                final EditText txtRename = new EditText(mActiveView.getContext());

                if(object instanceof Notesheet){
                    txtRename.setText(((Notesheet) object).getName());
                }
                else if(object instanceof Directory){
                    txtRename.setText(((Directory) object).getName());
                }

                builder
                        .setTitle(R.string.rename)
                        .setView(txtRename)
                        .setNegativeButton(
                                R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setPositiveButton(
                                R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newName = txtRename.getText().toString();
                                        if(object instanceof Notesheet){
                                            Notesheet notesheet = (Notesheet) object;
                                            NotesheetImpl renamedNotesheet = new NotesheetImpl();
                                            NotesheetFacade nf = new NotesheetFacade(
                                                    mActiveView.getContext());

                                            renamedNotesheet.fromNotesheet(notesheet);
                                            renamedNotesheet.setName(newName);
                                            nf.update(renamedNotesheet);
                                        }
                                        else if(object instanceof Directory){
                                            DirectoryImpl renamedDirectory = new DirectoryImpl();
                                            DirectoryFacade df =
                                                    new DirectoryFacade(mActiveView.getContext());

                                            renamedDirectory.fromDirectory(
                                                    (Directory) object
                                            );
                                            renamedDirectory.setName(newName);
                                            df.rename(renamedDirectory);
                                        }
                                        mAdapter.refresh();
                                        dialog.dismiss();
                                    }
                                }
                        ).show();
                break;
        }

        mAdapter.refresh();
        return false;
    }
}
