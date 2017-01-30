package at.htl_leonding.musicnotesync.presentation;

import android.graphics.Bitmap;

/**
 * Created by michael on 1/30/17.
 */

public class ImageViewModel {
    private final ImageViewController mController;
    private String filename;
    private Bitmap bitmap;
    private TouchImageView imageView;

    public ImageViewModel(ImageViewController imageViewController) {
        mController = imageViewController;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setImageView(TouchImageView imageView) {
        this.imageView = imageView;
    }

    public TouchImageView getImageView() {
        return imageView;
    }
}
