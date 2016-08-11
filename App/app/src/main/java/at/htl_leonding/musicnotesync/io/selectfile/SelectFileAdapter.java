package at.htl_leonding.musicnotesync.io.selectfile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import at.htl_leonding.musicnotesync.R;

/**
 * Created by michael on 27.07.16.
 */
public class SelectFileAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<File> mFiles;

    public class SelectFileViewHolder extends RecyclerView.ViewHolder{
        private TextView txtFileChooserFileName;

        public SelectFileViewHolder(View itemView)
        {
            super(itemView);
            this.txtFileChooserFileName =
                    (TextView) itemView.findViewById(R.id.txtFileChooserFileName);
        }
    }

    public SelectFileAdapter(Context context, List<File> files) {
        this.mContext = context;
        this.mFiles = files;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SelectFileViewHolder selectFileViewHolder = new SelectFileViewHolder(parent);
        return selectFileViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView txtFileName = ((SelectFileViewHolder) holder).txtFileChooserFileName;
        txtFileName.setText(mFiles.get(position).getAbsolutePath());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }
}
