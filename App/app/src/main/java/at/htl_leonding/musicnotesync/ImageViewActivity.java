package at.htl_leonding.musicnotesync;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomImageView customImageView = (CustomImageView) findViewById(R.id.noteSheetView);
        customImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.testimg));
        setContentView(R.layout.activity_image_view);
    }
}
