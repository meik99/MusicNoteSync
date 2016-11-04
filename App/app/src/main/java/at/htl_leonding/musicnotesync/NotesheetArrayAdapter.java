package at.htl_leonding.musicnotesync;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetLongClickListener;

/**
 * Created by hanne on 12.08.2016.
 */
public class NotesheetArrayAdapter extends RecyclerView.Adapter<NotesheetArrayAdapter.NotesheetViewHolder>{
    private static final String TAG = NotesheetArrayAdapter.class.getSimpleName();

    public class NotesheetViewHolder extends RecyclerView.ViewHolder{
        protected TextView nameView;
        protected ImageView iconView;

        public NotesheetViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView)itemView.findViewById(R.id.noteSheetNameView);
            iconView = (ImageView) itemView.findViewById(at.htl_leonding.musicnotesync.R.id.iconView);
        }
    }

    private List<Object> sheets;
    private MainController mController;
    private Activity mActivity;
    private Directory currDir;

    public void setDirectory(Directory dir) {
        this.sheets = new ArrayList<>();
        Log.d(TAG, "setDirectory: " + dir.getName());
        for (Directory d : mController.getDirectoryFacade().getChildren(dir)){
            sheets.add((Object)d);
        }

        for (Notesheet s : mController.getNotesheets(dir)) {
            sheets.add((Object)s);
        }
        currDir = dir;
        this.notifyDataSetChanged();
    }

    public Directory getCurrentDirectory() {
        return currDir;
    }

    public NotesheetArrayAdapter(MainController controller, Activity activity) {
        this.mController = controller;
        this.sheets = new ArrayList<>();
        this.mActivity = activity;
        this.currDir = mController.getDirectoryFacade().getRoot();

        for (Directory d : mController.getDirectoryFacade().getChildren(mController.getDirectoryFacade().getRoot())){
            if (d != null)
                sheets.add((Object)d);
        }

        for (Notesheet s : mController.getNotesheets(mController.getDirectoryFacade().getRoot())) {
            if (s != null)
                sheets.add((Object)s);
        }

    }

    @Override
    public NotesheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.notesheet_list_item, parent, false);

        itemView.setOnClickListener(new NotesheetClickListener(mController, this));
        itemView.setOnLongClickListener(new NotesheetLongClickListener(mController, this, mActivity));

        NotesheetViewHolder result = new NotesheetViewHolder(itemView);

        return result;
    }

    @Override
    public void onBindViewHolder(NotesheetViewHolder holder, int position) {
        Object s = sheets.get(position);
        if (s instanceof Notesheet) {
            holder.nameView.setText(((Notesheet) s).getName());
            holder.itemView.setTag(s);
            holder.iconView.setImageResource(R.drawable.ic_audiotrack_black_24dp);
        } else {
            holder.nameView.setText(((Directory)s).getName());
            holder.itemView.setTag(s);
            holder.iconView.setImageResource(R.drawable.ic_select_file);
        }
    }

    @Override
    public int getItemCount() {
        return sheets.size();
    }


}
