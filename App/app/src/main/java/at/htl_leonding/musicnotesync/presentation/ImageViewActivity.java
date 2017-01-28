package at.htl_leonding.musicnotesync.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;

import at.htl_leonding.musicnotesync.R;

public class ImageViewActivity extends AppCompatActivity {

    private static final String TAG = ImageViewActivity.class.getSimpleName();
    public static final String EXTRA_PATH_NAME = "pathName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        TouchImageView customImageView = (TouchImageView) findViewById(R.id.noteSheetView);

        String filename = this.getIntent().getStringExtra(EXTRA_PATH_NAME);
        Bitmap bb = BitmapFactory.decodeFile(
                getApplicationContext().getFilesDir().getPath() + File.separator + filename);
        Log.i(TAG, "onCreate: "
                +  getApplicationContext().getFilesDir().getPath() + File.separator + filename);
        if(bb != null) {
            long width = bb.getWidth();
            long height = bb.getHeight();
            double scale = (double) width / height;
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            double newWidth = metrics.widthPixels;
            System.out.println("Width: " + newWidth);
            long newHeight = (int) Math.round(newWidth / scale);

            customImageView.setImageBitmap(Bitmap.createScaledBitmap(bb, (int)newWidth, (int)newHeight, false));
        }
        else {
            customImageView.setImageBitmap(bb);
        }

        customImageView.invalidate();

    }
}
