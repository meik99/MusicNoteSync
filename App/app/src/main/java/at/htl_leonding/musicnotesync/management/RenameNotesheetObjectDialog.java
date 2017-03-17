package at.htl_leonding.musicnotesync.management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import at.htl_leonding.musicnotesync.mainactivity.MainController;
import at.htl_leonding.musicnotesync.R;

/**
 * Created by michael on 11/28/16.
 */

public class RenameNotesheetObjectDialog
        extends AlertDialog
        implements DialogInterface.OnClickListener {

    private final EditText txtNewName;
    private final MainController mMainController;

    public RenameNotesheetObjectDialog(Context context, MainController mainController) {
        super(context);

        txtNewName = new EditText(context);
        mMainController = mainController;

        this.setTitle(R.string.rename);
        this.setView(txtNewName);
        this.setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), this);
        this.setButton(BUTTON_POSITIVE, context.getString(R.string.ok), this);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == BUTTON_NEGATIVE){
            dialog.dismiss();
        }else{
            mMainController.renameNotesheetObject(txtNewName.getText().toString());
            dialog.dismiss();
        }
    }
}
