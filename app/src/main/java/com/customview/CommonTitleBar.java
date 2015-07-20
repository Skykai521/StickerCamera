package com.customview;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;


/**
 * 首页标题栏
 * 
 * @author ouyezi
 */
public class CommonTitleBar extends RelativeLayout {

    // 防重复点击时间
    private static final int BTN_LIMIT_TIME = 500;

    private TextView         leftButton;
    private ImageView        leftButtonImg;
    private TextView         middleButton;
    private TextView         rightButton;
    private ImageView        rightButtonImg;

    private int              leftBtnIconId;
    private String           leftBtnStr;
    private String           titleTxtStr;
    private String           rightBtnStr;
    private int              rightBtnIconId;

    private String           bgStyle;             //RED,WHITE 默认RED.红底与白底的

    public CommonTitleBar(Context context) {
        super(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        // 如果后续有文字按钮，可使用该模式设置
        leftBtnStr = arr.getString(R.styleable.CommonTitleBar_leftBtnTxt);
        leftBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_leftBtnIcon, 0);
        titleTxtStr = arr.getString(R.styleable.CommonTitleBar_titleTxt);
        rightBtnStr = arr.getString(R.styleable.CommonTitleBar_rightBtnTxt);
        rightBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_rightBtnIcon, 0);
        bgStyle = arr.getString(R.styleable.CommonTitleBar_baseStyle);
        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
            return;
        }

        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
        findViewById(R.id.title_out_frame).setBackgroundResource(R.color.pink);
        arr.recycle();
    }

    protected void onFinishInflate() {
        if (isInEditMode()) {
            return;
        }
        leftButtonImg = (ImageView) findViewById(R.id.title_left_btn);
        leftButton = (TextView) findViewById(R.id.title_left);
        middleButton = (TextView) findViewById(R.id.title_middle);
        rightButtonImg = (ImageView) findViewById(R.id.title_right_btn);
        rightButton = (TextView) findViewById(R.id.title_right);

        if (leftBtnIconId != 0) {
            leftButtonImg.setImageResource(leftBtnIconId);
            leftButtonImg.setVisibility(View.VISIBLE);
        } else {
            leftButtonImg.setVisibility(View.GONE);
        }
        if (rightBtnIconId != 0) {
            rightButtonImg.setImageResource(rightBtnIconId);
            rightButtonImg.setVisibility(View.VISIBLE);
        } else {
            rightButtonImg.setVisibility(View.GONE);
        }
        setLeftTxtBtn(leftBtnStr);
        setTitleTxt(titleTxtStr);
        setRightTxtBtn(rightBtnStr);
    }

    public void setRightTxtBtn(String btnTxt) {
        if (!TextUtils.isEmpty(btnTxt)) {
            rightButton.setText(btnTxt);
            rightButton.setVisibility(View.VISIBLE);
        } else {
            rightButton.setVisibility(View.GONE);
        }
    }

    public void setLeftTxtBtn(String leftBtnStr) {
        if (!TextUtils.isEmpty(leftBtnStr)) {
            leftButton.setText(leftBtnStr);
            leftButton.setVisibility(View.VISIBLE);
        } else {
            leftButton.setVisibility(View.GONE);
        }
    }

    public void setTitleTxt(String title) {
        if (!TextUtils.isEmpty(title)) {
            middleButton.setText(title);
            middleButton.setVisibility(View.VISIBLE);
        } else {
            middleButton.setVisibility(View.GONE);
        }
    }

    public void hideLeftBtn() {
        leftButton.setVisibility(View.GONE);
        leftButtonImg.setVisibility(View.GONE);
        findViewById(R.id.title_left_area).setOnClickListener(null);
    }

    public void hideRightBtn() {
        rightButton.setVisibility(View.GONE);
        rightButtonImg.setVisibility(View.GONE);
        findViewById(R.id.title_right_area).setOnClickListener(null);
    }

    public void setLeftBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_left_area).setOnClickListener(myListener);
    }

    public void setRightBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_right_area).setOnClickListener(myListener);
    }

}
