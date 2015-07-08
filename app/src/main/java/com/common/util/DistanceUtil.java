package com.common.util;

import com.stickercamera.App;

public class DistanceUtil {

    public static int getCameraAlbumWidth() {
        return (App.getApp().getScreenWidth() - App.getApp().dp2px(10)) / 4 - App.getApp().dp2px(4);
    }
    
    // 相机照片列表高度计算 
    public static int getCameraPhotoAreaHeight() {
        return getCameraPhotoWidth() + App.getApp().dp2px(4);
    }
    
    public static int getCameraPhotoWidth() {
        return App.getApp().getScreenWidth() / 4 - App.getApp().dp2px(2);
    }

    //活动标签页grid图片高度
    public static int getActivityHeight() {
        return (App.getApp().getScreenWidth() - App.getApp().dp2px(24)) / 3;
    }
}
