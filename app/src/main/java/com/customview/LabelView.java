package com.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;
import com.stickercamera.App;
import com.stickercamera.AppConstants;
import com.stickercamera.app.camera.util.EffectUtil;
import com.stickercamera.app.model.TagItem;


/**
 * @author tongqian.ni
 *
 */
public class LabelView extends LinearLayout {

    private TagItem tagInfo      = new TagItem();
    private float     parentWidth  = 0;
    private float     parentHeight = 0;
    private ImageView labelIcon;
    private TextView  labelTxtLeft;
    private TextView  labelTxtRight;

    public TagItem getTagInfo() {
        return tagInfo;
    }

    public LabelView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_label, this);
        labelIcon = (ImageView) findViewById(R.id.label_icon);
        labelTxtLeft = (TextView) findViewById(R.id.label_text_left);
        labelTxtRight = (TextView) findViewById(R.id.label_text_right);
    }

    public LabelView(Context context, AttributeSet attr) {
        super(context, attr);
        LayoutInflater.from(context).inflate(R.layout.view_label, this);
        labelIcon = (ImageView) findViewById(R.id.label_icon);
        labelTxtLeft = (TextView) findViewById(R.id.label_text_left);
        labelTxtRight = (TextView) findViewById(R.id.label_text_right);
    }

    public void init(TagItem tagItem) {
        tagInfo.setName(tagItem.getName());
        tagInfo.setId(tagItem.getId());
        tagInfo.setType(tagItem.getType());
        labelTxtLeft.setText(tagItem.getName());
        labelTxtRight.setText(tagItem.getName());
        if (tagItem.getType() == AppConstants.POST_TYPE_POI) {
            labelIcon.setImageResource(R.drawable.point_poi);
        }
    }

    /**
     * 将标签放置于对应RelativeLayout的对应位置，考虑引入postion作为参数？？
     * @param parent
     * @param left
     * @param top
     */
    public void draw(ViewGroup parent, final int left, final int top, boolean isLeft) {
        this.parentWidth = parent.getWidth();
        if (parentWidth <= 0) {
            parentWidth = App.getApp().getScreenWidth();
        }
        setImageWidth((int) parentWidth);
        this.parentHeight = parentWidth;
        if (isLeft) {
            labelTxtRight.setVisibility(View.VISIBLE);
            labelTxtLeft.setVisibility(View.GONE);
            setupLocation(left, top);
            parent.addView(this);
        } else {
            labelTxtRight.setVisibility(View.GONE);
            labelTxtLeft.setVisibility(View.VISIBLE);
            setupLocation(left, top);
            parent.addView(this);
        }

    }

    /**
     * 将标签放置于对应RelativeLayout的对应位置，考虑引入postion作为参数？？
     * @param parent
     * @param left
     * @param top
     */
    public void addTo(ViewGroup parent, final int left, final int top) {
        if (left > parent.getWidth() / 2) {
            tagInfo.setLeft(false);
        }
        this.parentWidth = parent.getWidth();
        if (parentWidth <= 0) {
            parentWidth = App.getApp().getScreenWidth();
        }
        setImageWidth((int) parentWidth);
        this.parentHeight = parentWidth;
        if (emptyItem) {
            labelTxtRight.setVisibility(View.GONE);
            labelTxtLeft.setVisibility(View.GONE);
            setupLocation(left, top);
            parent.addView(this);
        } else if (tagInfo.isLeft()) {
            labelTxtRight.setVisibility(View.VISIBLE);
            labelTxtLeft.setVisibility(View.GONE);
            setupLocation(left, top);
            parent.addView(this);
        } else {
            labelTxtRight.setVisibility(View.GONE);
            labelTxtLeft.setVisibility(View.INVISIBLE);
            setupLocation(20, 20);
            parent.addView(this);

            post(new Runnable() {
                @Override
                public void run() {
                    int toLeft = left - getWidth() + labelIcon.getWidth();
                    setupLocation(toLeft, top);
                    labelTxtLeft.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    private void setupLocation(int leftLoc, int topLoc) {
        this.left = leftLoc;
        this.top = topLoc;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (getImageWidth() - left - getWidth() < 0) {
            left = getImageWidth() - getWidth();
        }
        if (getImageWidth() - top - getHeight() < 0) {
            top = getImageWidth() - getHeight();
        }
        if (left < 0 && top < 0) {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (left < 0) {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMargins(0, top, 0, 0);
        } else if (top < 0) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.setMargins(left, 0, 0, 0);
        } else {
            params.setMargins(left, top, 0, 0);
        }

        tagInfo.setX(EffectUtil.getStandDis(left, this.parentWidth));
        tagInfo.setY(EffectUtil.getStandDis(top, this.parentHeight));
        setLayoutParams(params);
    }

    private void setImageWidth(int width) {
        this.imageWidth = width;
    }

    private int getImageWidth() {
        return imageWidth <= 0 ? App.getApp().getScreenWidth() : imageWidth;
    }

    private int left       = -1, top = -1;
    private int imageWidth = 0;

    private static final int ANIMATIONEACHOFFSET = 600;

    private boolean          emptyItem           = false;

    public void setEmpty() {
        emptyItem = true;
        labelTxtLeft.setVisibility(View.GONE);
        labelTxtRight.setVisibility(View.GONE);
    }

    public void wave() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 2f, 1f, 2f, ScaleAnimation.RELATIVE_TO_SELF,
            0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIMATIONEACHOFFSET * 3);
        sa.setRepeatCount(10);// 设置循环
        AlphaAnimation aniAlp = new AlphaAnimation(1, 0.1f);
        aniAlp.setRepeatCount(10);// 设置循环
        as.setDuration(ANIMATIONEACHOFFSET * 3);
        as.addAnimation(sa);
        as.addAnimation(aniAlp);
        labelIcon.startAnimation(as);
    }

    public void updateLocation(int x, int y) {
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        setupLocation(x, y);
        wave();
    }
}
