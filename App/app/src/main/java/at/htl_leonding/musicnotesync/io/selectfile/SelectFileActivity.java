package at.htl_leonding.musicnotesync.io.selectfile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import at.htl_leonding.musicnotesync.R;

public class SelectFileActivity extends AppCompatActivity {

    RecyclerView fileChooserRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        fileChooserRecyclerView = (RecyclerView) findViewById(R.id.fileChooserRecyclerView);
        fileChooserRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fileChooserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileChooserRecyclerView.setAdapter(new SelectFileAdapter());

    }
}
