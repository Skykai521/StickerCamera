package com.customview.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

public interface FeatherDrawable {
    public void setMinSize(float paramFloat1, float paramFloat2);

    public float getMinWidth();

    public float getMinHeight();

    public boolean validateSize(RectF paramRectF);

    public void draw(Canvas paramCanvas);

    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    public void setBounds(Rect paramRect);

    public void copyBounds(Rect paramRect);

    public Rect copyBounds();

    public Rect getBounds();

    public void setChangingConfigurations(int paramInt);

    public int getChangingConfigurations();

    public void setDither(boolean paramBoolean);

    public void setFilterBitmap(boolean paramBoolean);

    public void setCallback(Drawable.Callback paramCallback);

    public void invalidateSelf();

    public void scheduleSelf(Runnable paramRunnable, long paramLong);

    public void unscheduleSelf(Runnable paramRunnable);

    public void setAlpha(int paramInt);

    public void setColorFilter(ColorFilter paramColorFilter);

    public void setColorFilter(int paramInt, PorterDuff.Mode paramMode);

    public void clearColorFilter();

    public boolean isStateful();

    public boolean setState(int[] paramArrayOfInt);

    public int[] getState();

    public Drawable getCurrent();

    public boolean setLevel(int paramInt);

    public int getLevel();

    public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2);

    public boolean isVisible();

    public int getOpacity();

    public Region getTransparentRegion();

    public float getCurrentWidth();

    public float getCurrentHeight();

    public int getMinimumWidth();

    public int getMinimumHeight();

    public boolean getPadding(Rect paramRect);

    public Drawable mutate();
}