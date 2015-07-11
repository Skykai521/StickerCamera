package com.stickercamera.app.camera.ui;

import android.os.Bundle;

import com.github.skykai.stickercamera.R;
import com.stickercamera.app.camera.CameraBaseActivity;

import butterknife.ButterKnife;

/**
 * 图片处理界面
 * Created by sky on 2015/7/8.
 */



public class PhotoProcessActivity extends CameraBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        ButterKnife.inject(this);


    }
}
