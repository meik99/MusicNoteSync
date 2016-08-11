package at.htl_leonding.musicnotesync.io.selectfile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.io.Storage;

public class SelectFileActivity extends AppCompatActivity {

    RecyclerView mFileChooserRecyclerView;
    Storage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        mStorage = new Storage(this);

        mFileChooserRecyclerView = (RecyclerView) findViewById(R.id.fileChooserRecyclerView);
        mFileChooserRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFileChooserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFileChooserRecyclerView.setAdapter(new SelectFileAdapter(this,
                mStorage.getDirectoryContent(null)));

    }
}
