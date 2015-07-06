package com.stickercamera.base;

import android.app.Activity;
import android.view.View;
import com.github.skykai.stickercamera.R;
import com.customview.CommonTitleBar;

/**
 * Created by sky on 15/7/6.
 */
public class BaseActivity extends Activity {

    protected CommonTitleBar titleBar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        titleBar = (CommonTitleBar) findViewById(R.id.title_layout);
        if (titleBar != null)
            titleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
    }
}
