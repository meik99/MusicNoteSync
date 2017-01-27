package at.htl_leonding.musicnotesync.presentation;

import android.graphics.PointF;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * Created by hanne on 26.01.2017.
 */

public class TouchImageViewListener implements TouchImageView.OnTouchImageViewListener{
    private ImageView image;
    private List<EventListener> eventListeners = new ArrayList<>();

    public void addObserver(EventListener listener) {
            this.eventListeners.add(listener);
    }

    public void removeObserver(EventListener listener) {
        this.eventListeners.remove(listener);
    }

    public TouchImageViewListener(TouchImageView img) {
        this.image = img;
    }

    @Override
    public void onMove() {
        System.out.println("changed: " + image.getHeight());
    }

}
