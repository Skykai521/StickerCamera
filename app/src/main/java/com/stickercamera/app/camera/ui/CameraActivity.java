package com.stickercamera.app.camera.ui;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.github.skykai.stickercamera.R;
import com.stickercamera.app.camera.CameraBaseActivity;

import butterknife.ButterKnife;

/**
 * 相机界面
 * Created by sky on 15/7/6.
 */
public class CameraActivity extends CameraBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {

    }
}
