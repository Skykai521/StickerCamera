package com.stickercamera.app.camera.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class UIUtils {

    public static final int HIGHLIGHT_MODE_PRESSED  = 2;
    public static final int HIGHLIGHT_MODE_CHECKED  = 4;
    public static final int HIGHLIGHT_MODE_SELECTED = 8;

    public static final int GLOW_MODE_PRESSED       = 2;
    public static final int GLOW_MODE_CHECKED       = 4;
    public static final int GLOW_MODE_SELECTED      = 8;

    public static boolean checkBits(int status, int checkBit) {
        return (status & checkBit) == checkBit;
    }

    /**
     * Creates a custom {@link Toast} with a custom layout View
     * 
     * @param context
     *            the context
     * @param resId
     *            the custom view
     * @return the created {@link Toast}
     */
    public static Toast makeCustomToast(Context context, int resId) {
        View view = LayoutInflater.from(context).inflate(resId, null);
        Toast t = new Toast(context);
        t.setDuration(Toast.LENGTH_SHORT);
        t.setView(view);
        t.setGravity(Gravity.CENTER, 0, 0);
        return t;
    }

}
