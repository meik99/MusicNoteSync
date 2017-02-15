package at.htl_leonding.musicnotesync.presentation;

import android.widget.ImageView;

/**
 * Created by mrynkiewicz on 06/02/17.
 */

public class ZoomHandler {
    private final String[] mData;

    private boolean mValid = true;
    private String mScaleString;
    private String mFocusXString;
    private String mFocusYString;
    private String mScaleTypeString;
    private float mScale;
    private float mFocusX;
    private float mFocusY;
    private ImageView.ScaleType mScaleType;

    public ZoomHandler(String[] data){
        if(data == null){
            throw new IllegalArgumentException("data must not be null!");
        }

        mData = data;

        getStrings();
    }

    private void getStrings(){
        if(mData.length >= 5){
            mScaleString = mData[1];
            mFocusXString = mData[2];
            mFocusYString = mData[3];
            mScaleTypeString = mData[4];
        }else{
            mValid = false;
        }
    }

    private void convertStrings(){
        try {
            mScale = Float.parseFloat(mScaleString);
            mFocusX = Float.parseFloat(mFocusXString);
            mFocusY = Float.parseFloat(mFocusYString);
            mScaleType = convertScaleType();
        }catch(NumberFormatException ex){
            mValid = false;
        }
    }

    private ImageView.ScaleType convertScaleType() {
        ImageView.ScaleType[] types = ImageView.ScaleType.values();

        for (ImageView.ScaleType type: types) {
            if(type.name().equals(mScaleTypeString)){
                return type;
            }
        }

        return null;
    }

    public ImageView.ScaleType getScaleType() {
        return mScaleType;
    }

    public float getFocusY() {
        return mFocusY;
    }

    public float getFocusX() {
        return mFocusX;
    }

    public float getScale() {
        return mScale;
    }

    public boolean isValid() {
        return mValid;
    }
}
