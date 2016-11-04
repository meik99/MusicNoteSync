package at.htl_leonding.musicnotesync;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NameFolderActivity extends AppCompatActivity {
    private EditText textFolderName;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_folder);

        this.textFolderName = (EditText) findViewById(R.id.editFolderName);
        this.btnOk = (Button) findViewById(R.id.buttonAddOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textFolderName.getText().toString() != ""){
                    Intent intent = new Intent();
                    intent.putExtra("FolderName", textFolderName.getText().toString());
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
