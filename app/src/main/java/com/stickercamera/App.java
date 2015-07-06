package com.stickercamera;

import android.app.Application;

/**
 * Created by sky on 2015/7/6.
 */
public class App extends Application {

    protected static App       mInstance;

    public App(){
        mInstance = this;
    }

    public static App getApp() {
        if (mInstance != null && mInstance instanceof App) {
            return (App) mInstance;
        } else {
            mInstance = new App();
            mInstance.onCreate();
            return (App) mInstance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


}
