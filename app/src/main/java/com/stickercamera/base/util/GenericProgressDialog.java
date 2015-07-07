package com.stickercamera.base.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;


public class GenericProgressDialog extends AlertDialog {
    private ProgressBar  mProgress;
    private TextView     mMessageView;
    private CharSequence mMessage;
    private boolean      mIndeterminate;
    private boolean      mProgressVisiable;

    public GenericProgressDialog(Context context) {
        super(context/*,R.style.Float*/);
    }

    public GenericProgressDialog(Context context, int theme) {
        super(context,/*, R.style.Float*/theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_progress_dialog);
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mMessageView = (TextView) findViewById(R.id.message);

        setMessageAndView();
        setIndeterminate(mIndeterminate);
    }

    private void setMessageAndView() {
        mMessageView.setText(mMessage);

        if (mMessage == null || "".equals(mMessage)) {
            mMessageView.setVisibility(View.GONE);
        }

        mProgress.setVisibility(mProgressVisiable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setMessage(CharSequence message) {
        mMessage = message;
    }

    /**
     * 圈圈可见性设置
     * @param progressVisiable 是否显示圈圈
     */
    public void setProgressVisiable(boolean progressVisiable) {
        mProgressVisiable = progressVisiable;
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }
}
