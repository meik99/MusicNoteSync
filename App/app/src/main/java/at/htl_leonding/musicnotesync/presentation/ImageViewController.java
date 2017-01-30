package at.htl_leonding.musicnotesync.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.io.File;

import at.htl_leonding.musicnotesync.R;

import static at.htl_leonding.musicnotesync.presentation.ImageViewActivity.EXTRA_PATH_NAME;

/**
 * Created by michael on 1/30/17.
 */

public class ImageViewController implements OnTouchListener{
    private static final String TAG = ImageViewController.class.getSimpleName();
    private ImageViewActivity mActivity;
    private ImageViewModel mModel;

    public ImageViewController(ImageViewActivity activity){
        mActivity = activity;
        mModel = new ImageViewModel(this);
        mModel.setImageView(
                (TouchImageView) mActivity.findViewById(R.id.noteSheetView));

        getFilenameFromIntent();
        getFileAsBitmap();
        setupImageView();

        mModel.getImageView().setOnTouchListener(this);
        mModel.getImageView().invalidate();
    }

    private void setupImageView(){
        Bitmap bitmap = mModel.getBitmap();
        TouchImageView imageView = mModel.getImageView();

        if(bitmap != null){
            double scale = bitmap.getWidth() / bitmap.getHeight();
            long newHeight = 0;
            DisplayMetrics metrics = new DisplayMetrics();

            mActivity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(metrics);

            newHeight = Math.round(metrics.widthPixels / scale);
            imageView.setImageBitmap(
                    Bitmap
                            .createScaledBitmap(bitmap,
                                    (int)metrics.widthPixels,
                                    (int)newHeight,
                                    false)
            );
        }else{
            imageView.setImageBitmap(bitmap);
        }
    }

    private void getFileAsBitmap() {
        String path = mActivity
                .getApplicationContext()
                .getFilesDir()
                .getPath() +
                File.separator +
                mModel.getFilename();
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        Log.i(TAG, "onCreate: " + path);

        mModel.setBitmap(bitmap);
    }

    private void getFilenameFromIntent(){
        if(mActivity.getIntent() != null){
            if(mActivity.getIntent().hasExtra(
                    ImageViewActivity.EXTRA_PATH_NAME)){
                String filename = mActivity.getIntent()
                        .getStringExtra(EXTRA_PATH_NAME);

                mModel.setFilename(filename);
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_HOVER_ENTER:
                System.out.println("hover enter");
                break;
        }
        return false;
    }
}
