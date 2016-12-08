package at.htl_leonding.musicnotesync.management.move;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import at.htl_leonding.musicnotesync.MainActivity;
import at.htl_leonding.musicnotesync.MainController;
import at.htl_leonding.musicnotesync.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.db.contract.Directory;

public class MoveActivity extends AppCompatActivity {
    RecyclerView mNoteSheetRecyclerView;
    Button btnMoveDirectory;

    private MoveController mMoveController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_file);
        mMoveController = new MoveController(this);

        mNoteSheetRecyclerView = (RecyclerView) findViewById(R.id.moveRecyclerView);
        mNoteSheetRecyclerView.setAdapter(mMoveController.getNotesheetArrayAdapter());
        mNoteSheetRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );

        btnMoveDirectory = (Button) findViewById(R.id.btnMoveOk);
        btnMoveDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoveController.moveObjectToDirectory();
                finish();
            }
        });
    }
}
