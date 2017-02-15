package at.htl_leonding.musicnotesync.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

import at.htl_leonding.musicnotesync.R;

public class ImageViewActivity extends AppCompatActivity {

    private static final String TAG = ImageViewActivity.class.getSimpleName();
    public static final String EXTRA_PATH_NAME = "pathName";
    public static final String EXTRA_CLIENTS = "clients";

    private ImageViewController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        mController = new ImageViewController(this);
    }
}
