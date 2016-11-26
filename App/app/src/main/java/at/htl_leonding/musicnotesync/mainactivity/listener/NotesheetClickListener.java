package at.htl_leonding.musicnotesync.mainactivity.listener;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import at.htl_leonding.musicnotesync.presentation.ImageViewActivity;
import at.htl_leonding.musicnotesync.MainController;
import at.htl_leonding.musicnotesync.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;

/**
 * Created by michael on 02.09.16.
 */
public class NotesheetClickListener implements View.OnClickListener {
    private MainController mController;
    private NotesheetArrayAdapter mAdapter;

    public NotesheetClickListener(MainController controller, NotesheetArrayAdapter adapter) {
        mController = controller;
        mAdapter = adapter;
    }

    @Override
    public void onClick(View itemView) {
        if(itemView.getTag() != null && !itemView.getTag().equals("Dir")) {
            Object object = (Object) itemView.getTag();
            Intent intent = new Intent(itemView.getContext(), ImageViewActivity.class);

            DirectoryFacade df = new DirectoryFacade(itemView.getContext());
            if (object instanceof Notesheet) {
                Notesheet notesheet = (Notesheet)object;
                mController.openNotesheet(notesheet);

                intent.putExtra("pathName", notesheet.getPath());
                itemView.getContext().startActivity(intent);
            }
            else{
                mAdapter.setDirectory((Directory)object);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


}
