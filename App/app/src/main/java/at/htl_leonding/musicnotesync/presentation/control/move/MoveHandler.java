package at.htl_leonding.musicnotesync.presentation.control.move;

import android.util.Log;

/**
 * Created by michael on 3/9/17.
 */
public class MoveHandler {
    private static final String TAG = MoveHandler.class.getSimpleName();

    private String xString;
    private String yString;
    private String rightString;
    private String bottomString;

    private float x;
    private float y;
    private float right;
    private float bottom;

    private boolean valid = true;
    public MoveHandler(String[] data){
        if(data.length < 3){
            valid = false;
        }else{
            initStrings(data);
            initFloats();
        }
    }

    private void initFloats() {
        try{
            x = Float.parseFloat(xString);
            y = Float.parseFloat(yString);
            right = Float.parseFloat(rightString);
            bottom = Float.parseFloat(bottomString);
        }catch(NumberFormatException e){
            valid = false;
            Log.e(TAG, "initFloats: " + e.getMessage());
        }
    }

    private void initStrings(String[] data) {
        xString = data[1];
        yString = data[2];
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public boolean isValid() {
        return valid;
    }
}
