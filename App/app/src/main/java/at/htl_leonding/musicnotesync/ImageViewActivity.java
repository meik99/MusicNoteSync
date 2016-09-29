package at.htl_leonding.musicnotesync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import at.htl_leonding.musicnotesync.io.Storage;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        CustomImageView customImageView = (CustomImageView) findViewById(R.id.noteSheetView);

        String filename = this.getIntent().getStringExtra("pathName");
        Bitmap bb = BitmapFactory.decodeFile(
                getApplicationContext().getFilesDir() + File.separator + filename);

        /*int width = bb.getWidth();
        int height = bb.getHeight();
        double scale = (double)width / height;
        int newWidth = 4096;
        int newHeight = (int)Math.round(newWidth / scale);

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(newWidth / (float)width, newHeight / (float)height);*/

//        Bitmap nb = Bitmap.createBitmap(bb, 0, 0, newWidth, newHeight, scaleMatrix, true);

        customImageView.setImageBitmap(bb);
        customImageView.invalidate();
    }
}
