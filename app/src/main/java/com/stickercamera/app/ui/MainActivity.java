package com.stickercamera.app.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.skykai.stickercamera.R;
import com.melnykov.fab.FloatingActionButton;
import com.stickercamera.app.camera.CameraManager;
import com.stickercamera.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.fab)
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initView();
    }

    private void initView(){
        fab.setOnClickListener(v -> CameraManager.getInst().openCamera(MainActivity.this));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
//            showProgressDialog("test");
//            toast("1122",5000);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
