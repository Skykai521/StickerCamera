package com.customview;

import android.view.View;
import android.view.View.OnClickListener;


/**
 * 全局拒绝频繁点击代理Listener
 * 
 * @author tongqian.ni
 */
public class GlobalLimitClickOnClickListener implements OnClickListener {

    // 全局防频繁点击
    private static long     lastClick;

    private OnClickListener listener;

    private long            intervalClick;

    public GlobalLimitClickOnClickListener(OnClickListener listener, long intervalClick) {
        this.intervalClick = intervalClick;
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() > lastClick
            && System.currentTimeMillis() - lastClick <= intervalClick) {
            return;
        }
        listener.onClick(v);
        lastClick = System.currentTimeMillis();
    }
}
