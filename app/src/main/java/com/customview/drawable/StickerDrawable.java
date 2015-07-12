package com.customview.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class StickerDrawable extends BitmapDrawable implements FeatherDrawable {
    private float  minWidth    = 0.0F;
    private float  minHeight   = 0.0F;
    BlurMaskFilter mBlurFilter;
    Paint          mShadowPaint;
    Bitmap         mShadowBitmap;
    boolean        mDrawShadow = true;
    Rect           mTempRect   = new Rect();

    public StickerDrawable(Resources resources, Bitmap bitmap) {
        super(resources, bitmap);

        this.mBlurFilter = new BlurMaskFilter(5.0F, BlurMaskFilter.Blur.OUTER);
        this.mShadowPaint = new Paint(1);
        this.mShadowPaint.setMaskFilter(this.mBlurFilter);

        int[] offsetXY = new int[2];
        this.mShadowBitmap = getBitmap().extractAlpha(this.mShadowPaint, offsetXY);
    }

    public int getBitmapWidth() {
        return getBitmap().getWidth();
    }

    public int getBitmapHeight() {
        return getBitmap().getHeight();
    }

    public void draw(Canvas canvas) {
        if (this.mDrawShadow) {
            copyBounds(this.mTempRect);
            canvas.drawBitmap(this.mShadowBitmap, null, this.mTempRect, null);
        }
        super.draw(canvas);
    }

    public void setDropShadow(boolean value) {
        this.mDrawShadow = value;
        invalidateSelf();
    }

    public boolean validateSize(RectF rect) {
        return (rect.width() >= this.minWidth) && (rect.height() >= this.minHeight);
    }

    public void setMinSize(float w, float h) {
        this.minWidth = w;
        this.minHeight = h;
    }

    public float getMinWidth() {
        return this.minWidth;
    }

    public float getMinHeight() {
        return this.minHeight;
    }

    public float getCurrentWidth() {
        return getIntrinsicWidth();
    }

    public float getCurrentHeight() {
        return getIntrinsicHeight();
    }
}
