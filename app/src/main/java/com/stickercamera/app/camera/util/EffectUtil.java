package com.stickercamera.app.camera.util;

import com.stickercamera.App;
import com.stickercamera.AppConstants;

/**
 * Created by sky on 15/7/6.
 */
public class EffectUtil {

    public static int getStandDis(float realDis, float baseWidth) {
        float imageWidth = baseWidth <= 0 ? App.getApp().getScreenWidth() : baseWidth;
        float radio = AppConstants.DEFAULT_PIXEL / imageWidth;
        return (int) (radio * realDis);
    }

}
