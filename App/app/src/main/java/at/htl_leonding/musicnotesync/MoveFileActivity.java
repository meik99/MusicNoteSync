package at.htl_leonding.musicnotesync;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import at.htl_leonding.musicnotesync.db.contract.Directory;

public class MoveFileActivity extends AppCompatActivity {
    RecyclerView mNoteSheetRecyclerView;
    NotesheetArrayAdapter mAdapter;
    MainController mController;
    Button btnMoveDirectory;

    static Directory dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_file);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mController = MainActivity.getMainController();
        mAdapter = new NotesheetArrayAdapter(mController, this);
        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.moveRecyclerView);
        mNoteSheetRecyclerView.setAdapter(mAdapter);
        mNoteSheetRecyclerView.setLayoutManager(llm);

        dir = mController.getDirectoryFacade().getRoot();

        btnMoveDirectory = (Button) findViewById(R.id.btnMoveOk);
        btnMoveDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dir = mAdapter.getCurrentDirectory();
                finish();
            }
        });
    }

    public static Directory getTargetDirectory(){
        return dir;
    }
}
