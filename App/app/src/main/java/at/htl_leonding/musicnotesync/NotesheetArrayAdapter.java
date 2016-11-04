package at.htl_leonding.musicnotesync;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.mainactivity.listener.NotesheetClickListener;

/**
 * Created by hanne on 12.08.2016.
 */
public class NotesheetArrayAdapter extends RecyclerView.Adapter<NotesheetArrayAdapter.NotesheetViewHolder>{

    public class NotesheetViewHolder extends RecyclerView.ViewHolder{
        protected TextView nameView;

        public NotesheetViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView)itemView.findViewById(R.id.noteSheetNameView);
        }
    }

    private List<Notesheet> sheets;
    private MainController mController;

    public void setSheets(List<Notesheet> sheets) {
        this.sheets = sheets;
        this.notifyDataSetChanged();
    }

    public NotesheetArrayAdapter(MainController controller) {
        this.mController = controller;
        this.sheets = mController.getNotesheets(null);
    }

    @Override
    public NotesheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.notesheet_list_item, parent, false);

        itemView.setOnClickListener(new NotesheetClickListener(mController, this));

        NotesheetViewHolder result = new NotesheetViewHolder(itemView);

        return result;
    }

    @Override
    public void onBindViewHolder(NotesheetViewHolder holder, int position) {
        Notesheet s = sheets.get(position);
        holder.nameView.setText(s.getName());
        holder.itemView.setTag(s);
    }

    @Override
    public int getItemCount() {
        return sheets.size();
    }


}
