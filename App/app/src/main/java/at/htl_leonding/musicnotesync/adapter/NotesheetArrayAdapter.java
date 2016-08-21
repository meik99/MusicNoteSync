package at.htl_leonding.musicnotesync.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by hanne on 12.08.2016.
 */
public class NotesheetArrayAdapter extends RecyclerView.Adapter<NotesheetArrayAdapter.NotesheetViewHolder>{
    private List<Notesheet> sheets;

    public void setSheets(List<Notesheet> sheets) {
        this.sheets = sheets;
    }

    public NotesheetArrayAdapter(List<Notesheet> sheets) {
        this.sheets = sheets;
    }

    @Override
    public NotesheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notesheet_list_item, parent, false);
        return new NotesheetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotesheetViewHolder holder, int position) {
        Notesheet s = sheets.get(position);
        holder.nameView.setText(s.getName());
    }

    @Override
    public int getItemCount() {
        return sheets.size();
    }

    public class NotesheetViewHolder extends RecyclerView.ViewHolder{
        protected TextView nameView;
        public NotesheetViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView)itemView.findViewById(R.id.noteSheetNameView);
        }
    }
}
