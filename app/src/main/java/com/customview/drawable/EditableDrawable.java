package com.customview.drawable;

import android.graphics.Paint;

public interface EditableDrawable {
    public static final int CURSOR_BLINK_TIME = 400;

    public void setOnSizeChangeListener(OnSizeChange paramOnSizeChange);

    public void beginEdit();

    public void endEdit();

    public boolean isEditing();

    public CharSequence getText();

    public void setText(CharSequence paramCharSequence);

    public void setText(String paramString);

    public void setTextHint(CharSequence paramCharSequence);

    public void setTextHint(String paramString);

    public boolean isTextHint();

    public void setBounds(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    public void setTextColor(int paramInt);

    public int getTextColor();

    public float getTextSize();

    public float getFontMetrics(Paint.FontMetrics paramFontMetrics);

    public void setTextStrokeColor(int paramInt);

    public int getTextStrokeColor();

    public void setStrokeEnabled(boolean paramBoolean);

    public boolean getStrokeEnabled();

    public int getNumLines();

    public static interface OnSizeChange {
        public void onSizeChanged(EditableDrawable paramEditableDrawable, float paramFloat1,
                                  float paramFloat2, float paramFloat3, float paramFloat4);
    }
}
