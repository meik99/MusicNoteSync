package at.htl_leonding.musicnotesync.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Message;

import at.htl_leonding.musicnotesync.R;

/**
 * Created by michael on 1/28/17.
 */

public class NotesheetNotFoundAlertDialog extends AlertDialog {
    protected NotesheetNotFoundAlertDialog(Context context) {
        super(context);

        this.setTitle(R.string.notesheet_not_found);
        this.setMessage(context.getString(R.string.notesheet_not_found_message));
        this.setButton(BUTTON_NEUTRAL, context.getString(R.string.ok), Message.obtain());
    }


}
