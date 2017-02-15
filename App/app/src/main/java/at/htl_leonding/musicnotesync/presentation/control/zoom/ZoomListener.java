package at.htl_leonding.musicnotesync.presentation.control.zoom;

import at.htl_leonding.musicnotesync.presentation.TouchImageView;

/**
 * Created by mrynkiewicz on 06/02/17.
 */

public interface ZoomListener {
    void onZoomBegin(TouchImageView view);
    void onZoom(TouchImageView view);
    void onZoomEnd(TouchImageView view);
}
