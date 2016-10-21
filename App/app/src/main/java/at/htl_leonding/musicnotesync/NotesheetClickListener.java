package at.htl_leonding.musicnotesync;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

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
    public void onClick(View itemView) {
        if(itemView.getTag() != null) {
            Notesheet notesheet = (Notesheet) itemView.getTag();
            Intent intent = new Intent(itemView.getContext(), ImageViewActivity.class);

            mController.openNotesheet(notesheet);

            intent.putExtra("pathName",
                    ((TextView)itemView.findViewById(R.id.noteSheetNameView)).getText());
            itemView.getContext().startActivity(intent);
        }
    }
}
