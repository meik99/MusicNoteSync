package at.htl_leonding.musicnotesync.management.move;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import at.htl_leonding.musicnotesync.R;

public class MoveActivity extends AppCompatActivity {
    RecyclerView mNoteSheetRecyclerView;
    Button btnMoveDirectory;
    TextView textFileToMove;

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

        textFileToMove = (TextView) findViewById(R.id.textFileToMove);
        textFileToMove.setText("'" + mMoveController.getSelectedFileName() + "'");

        btnMoveDirectory = (Button) findViewById(R.id.btnMoveOk);
        btnMoveDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoveController.moveObjectToDirectory();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mMoveController.goToDirectoryParent() == false){
            super.onBackPressed();
        }
    }
}
