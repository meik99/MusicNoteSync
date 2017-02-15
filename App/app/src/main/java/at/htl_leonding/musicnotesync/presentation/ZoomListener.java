package at.htl_leonding.musicnotesync.presentation;

/**
 * Created by mrynkiewicz on 06/02/17.
 */

public interface ZoomListener {
    void onZoomBegin(TouchImageView view);
    void onZoom(TouchImageView view);
    void onZoomEnd(TouchImageView view);
}
