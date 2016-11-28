package at.htl_leonding.musicnotesync;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryImpl;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.management.ManagementOptionsClickListener;

/**
 * Created by hanne on 12.08.2016.
 */
public class NotesheetArrayAdapter extends RecyclerView.Adapter<NotesheetArrayAdapter.NotesheetViewHolder>{
    private static final String TAG = NotesheetArrayAdapter.class.getSimpleName();

    public class NotesheetViewHolder extends RecyclerView.ViewHolder{
        protected TextView nameView;
        protected ImageView iconView;
        protected ImageButton managementOptions;

        public NotesheetViewHolder(View itemView) {
            super(itemView);

            nameView =
                    (TextView)itemView.findViewById(R.id.noteSheetNameView);
            iconView =
                    (ImageView) itemView.findViewById(at.htl_leonding.musicnotesync.R.id.iconView);
            managementOptions =
                    (ImageButton) itemView.findViewById(R.id.btnManagementOptions);
        }
    }

    private List<Object> mNotesheetObjects;
    private MainController mMainController;

    public void setNotesheetObjects(List<Object> notesheetObjects) {
        mNotesheetObjects = notesheetObjects;
        this.notifyDataSetChanged();
    }

    public NotesheetArrayAdapter(MainController mainController) {
        mNotesheetObjects = new LinkedList<>();
        mMainController = mainController;
    }

    @Override
    public NotesheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.notesheet_list_item, parent, false);
        itemView.setOnClickListener(mMainController.getNotesheetItemClickListener());

        NotesheetViewHolder result = new NotesheetViewHolder(itemView);
        result.managementOptions.setOnClickListener(
                mMainController.getManagementOptionClickListener());

        return result;
    }

    @Override
    public void onBindViewHolder(NotesheetViewHolder holder, int position) {
        Object object = mNotesheetObjects.get(position);
        if (object instanceof Notesheet) {
            holder.nameView.setText(((Notesheet) object).getName());
            holder.iconView.setImageResource(R.drawable.ic_audiotrack_black_24dp);
        } else {
            holder.nameView.setText(((Directory)object).getName());
            holder.iconView.setImageResource(R.drawable.ic_select_file);
        }

        holder.itemView.setTag(object);
        holder.managementOptions.setTag(object);
    }

    @Override
    public int getItemCount() {
        return mNotesheetObjects.size();
    }


}
