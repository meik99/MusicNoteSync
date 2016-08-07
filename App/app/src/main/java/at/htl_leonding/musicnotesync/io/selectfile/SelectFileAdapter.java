package at.htl_leonding.musicnotesync.io.selectfile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by michael on 27.07.16.
 */
public class SelectFileAdapter extends RecyclerView.Adapter {
    public class SelectFileViewHolder extends RecyclerView.ViewHolder{

        public SelectFileViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
