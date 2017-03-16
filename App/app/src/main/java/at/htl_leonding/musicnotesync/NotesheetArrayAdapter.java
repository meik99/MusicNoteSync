package at.htl_leonding.musicnotesync;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.mainactivity.listener.ShareNotesheetClickListener;

/**
 * Created by hanne on 12.08.2016.
 */
public class NotesheetArrayAdapter extends RecyclerView.Adapter<NotesheetArrayAdapter.NotesheetViewHolder>{
    private static final String TAG = NotesheetArrayAdapter.class.getSimpleName();

    public class NotesheetViewHolder extends RecyclerView.ViewHolder{
        protected TextView nameView;
        protected ImageView iconView;
        protected ImageButton managementOptions;
        protected ImageButton share;

        public NotesheetViewHolder(View itemView) {
            super(itemView);

            nameView =
                    (TextView)itemView.findViewById(R.id.noteSheetNameView);
            iconView =
                    (ImageView) itemView.findViewById(at.htl_leonding.musicnotesync.R.id.iconView);
            managementOptions =
                    (ImageButton) itemView.findViewById(R.id.btnManagementOptions);
            share = (ImageButton) itemView.findViewById(R.id.btnShareNotesheetItem);
        }
    }

    private List<Object> mNotesheetObjects;
    private View.OnClickListener mNotesheetClickListener;
    private View.OnClickListener mManagementOptionsClickListener;

    public void setNotesheetObjects(List<Object> notesheetObjects) {
        mNotesheetObjects = new LinkedList<>();

        for(Object obj : notesheetObjects){
            if(obj instanceof Directory){
                Directory directory = (Directory)obj;
                if(directory.getName().equals("ROOT") == false){
                    mNotesheetObjects.add(obj);
                }
            }else{
                mNotesheetObjects.add(obj);
            }
        }

        this.notifyDataSetChanged();
    }

    public NotesheetArrayAdapter(View.OnClickListener notesheetClickListener,
                                 View.OnClickListener managementOptionsClickListener) {
        mNotesheetObjects = new LinkedList<>();
        mNotesheetClickListener = notesheetClickListener;
        mManagementOptionsClickListener = managementOptionsClickListener;
    }

    @Override
    public NotesheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.notesheet_list_item, parent, false);

        if(mNotesheetClickListener != null) {
            itemView.setOnClickListener(mNotesheetClickListener);
        }

        NotesheetViewHolder result = new NotesheetViewHolder(itemView);

        if(mManagementOptionsClickListener != null) {
            result.managementOptions.setOnClickListener(
                    mManagementOptionsClickListener);
        }else{
            result.managementOptions.setEnabled(false);
        }

        result.share.setOnClickListener(new ShareNotesheetClickListener());

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
            holder.iconView.setImageResource(R.drawable.ic_folder_black_24dp);
        }

        holder.itemView.setTag(object);
        holder.managementOptions.setTag(object);
        holder.share.setTag(object);
    }

    @Override
    public int getItemCount() {
        return mNotesheetObjects.size();
    }


}
