package com.customview.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class AviaryTextDrawable extends Drawable implements FeatherDrawable, EditableDrawable {
    static final String                   LOG_TAG         = "text-drawable";
    protected final Paint                 mPaint;
    protected final Paint                 mCursorPaint;
    protected final Paint                 mStrokePaint;
    protected final Paint                 mDebugPaint;
    protected int                         mPaintAlpha;
    protected int                         mStrokeAlpha;
    protected String                      mHintString;
    protected String                      mText           = "";
    protected final RectF                 mBoundsF        = new RectF(0.0F, 0.0F, 0.0F, 0.0F);
    protected boolean                     mEditing        = false;
    protected long                        mNow            = 0L;
    protected boolean                     mShowCursor     = false;
    protected final List<Integer>         linesBreak      = new ArrayList<Integer>();
    protected boolean                     mTextHint       = false;
    protected float                       mCursorWidth    = 2.0F;
    protected float                       mCursorDistance = 1.0F;
    protected boolean                     mStrokeEnabled  = true;
    protected float                       mDefaultTextSize;
    protected float                       mIntrinsicHeight;
    protected float                       mIntrinsicWidth;
    protected float                       mMinWidth;
    protected float                       mMinHeight;
    protected float                       mMinTextSize    = 14.0F;

    Paint.FontMetrics                     metrics         = new Paint.FontMetrics();
    final Rect                            lastRect        = new Rect();
    final RectF                           drawRect        = new RectF();
    private EditableDrawable.OnSizeChange mSizeChangeListener;

    public AviaryTextDrawable(String text, float textSize) {
        this(text, textSize, null);
    }

    public AviaryTextDrawable(String text, float textSize, Typeface typeface) {
        this.mPaint = new Paint(451);

        this.mDefaultTextSize = textSize;

        if (this.mMinTextSize > this.mDefaultTextSize) {
            this.mMinTextSize = (this.mDefaultTextSize - 1.0F);
        }

        this.mPaint.setDither(true);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setTextSize(textSize);

        if (typeface != null) {
            this.mPaint.setTypeface(typeface);
        }

        this.mCursorPaint = new Paint(this.mPaint);

        this.mStrokePaint = new Paint(this.mPaint);
        this.mStrokePaint.setStyle(Paint.Style.STROKE);
        this.mStrokePaint.setStrokeWidth(textSize / 10.0F);

        this.mDebugPaint = null;

        setTextColor(-1);
        setStrokeColor(-16777216);

        setText(text);

        computeMinSize();
    }

    public float getDefaultTextSize() {
        return this.mDefaultTextSize;
    }

    public void setCursorWidth(int size) {
        this.mCursorWidth = size;
    }

    public void setStrokeEnabled(boolean enabled) {
        this.mStrokeEnabled = enabled;
    }

    public boolean getStrokeEnabled() {
        return this.mStrokeEnabled;
    }

    public void setTextHint(CharSequence text) {
        setTextHint((String) text);
    }

    public void setTextHint(String text) {
        this.mText = (text == null ? "" : text);
        this.mTextHint = (text != null);
        this.mHintString = (text == null ? "" : text);
        onTextInvalidate();
    }

    public boolean isTextHint() {
        return this.mTextHint;
    }

    public float getMinWidth() {
        return 3.4028235E+38F;
    }

    public float getMinHeight() {
        return this.mMinHeight;
    }

    public void beginEdit() {
        this.mEditing = true;
    }

    public void endEdit() {
        this.mEditing = false;

        if (((getText() == null) || (getText().length() < 1)) && (this.mHintString != null))
            setTextHint(this.mHintString);
    }

    public float getCurrentHeight() {
        if (this.mIntrinsicHeight < 0.0F) {
            this.mIntrinsicHeight = computeTextHeight();
        }
        return this.mIntrinsicHeight;
    }

    public float getCurrentWidth() {
        if (this.mIntrinsicWidth < 0.0F) {
            this.mIntrinsicWidth = Math.max(this.mMinWidth, computeTextWidth());
        }
        return this.mIntrinsicWidth;
    }

    public int getIntrinsicWidth() {
        return (int) getCurrentWidth();
    }

    public int getIntrinsicHeight() {
        return (int) getCurrentHeight();
    }

    protected void invalidateSize() {
        this.mIntrinsicHeight = -1.0F;
        this.mIntrinsicWidth = -1.0F;
    }

    public int getOpacity() {
        return this.mPaint.getAlpha();
    }

    public CharSequence getText() {
        return this.mText;
    }

    public int getTextColor() {
        return this.mPaint.getColor();
    }

    public int getTextStrokeColor() {
        return this.mStrokePaint.getColor();
    }

    public void setTextStrokeColor(int color) {
        this.mStrokePaint.setColor(color);
    }

    public float getTextSize() {
        return this.mPaint.getTextSize();
    }

    public boolean isEditing() {
        return this.mEditing;
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    public void setBounds(float left, float top, float right, float bottom) {
        if ((left != this.mBoundsF.left) || (top != this.mBoundsF.top)
            || (right != this.mBoundsF.right) || (bottom != this.mBoundsF.bottom)) {
            this.mBoundsF.set(left, top, right, bottom);
            setTextSize(bottom - top);
        }
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        setBounds((float) left, (float) top, (float) right, (float) bottom);
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
        this.mStrokePaint.setColorFilter(cf);
        this.mCursorPaint.setColorFilter(cf);
    }

    public void setStrokeColor(int color) {
        this.mStrokePaint.setColor(color);
        this.mStrokeAlpha = this.mStrokePaint.getAlpha();
    }

    public void setText(CharSequence text) {
        setText((String) text);
    }

    public void setText(String text) {
        this.mText = text;
        this.mTextHint = false;
        onTextInvalidate();
    }

    public void setTextColor(int color) {
        this.mPaint.setColor(color);
        this.mCursorPaint.setColor(color);
        this.mPaintAlpha = this.mPaint.getAlpha();
    }

    public void setTextSize(float size) {
        if (size / getNumLines() != this.mPaint.getTextSize()) {
            int lines = getNumLines();
            this.mPaint.setTextSize(size / lines);
            this.mCursorPaint.setTextSize(size / lines);
            this.mStrokePaint.setTextSize(size / lines);
            this.mStrokePaint.setStrokeWidth(size / lines / 10.0F);
            invalidateSize();
            computeMinSize();
        }
    }

    public boolean validateSize(RectF rect) {
        if (rect.height() < this.mMinHeight)
            return false;
        return true;
    }

    public void setMinSize(float width, float height) {
    }

    public void setMinTextSize(float size) {
        this.mMinTextSize = size;
    }

    public float getMinTextSize() {
        return this.mMinTextSize;
    }

    public float getFontMetrics(Paint.FontMetrics metrics) {
        return this.mPaint.getFontMetrics(metrics);
    }

    protected void computeMinSize() {
        this.mMinWidth = getMinTextWidth();
        this.mMinHeight = (getMinTextSize() * getNumLines());
    }

    protected float getMinTextWidth() {
        float[] widths = new float[1];
        this.mPaint.getTextWidths(" ", widths);
        return widths[0] / 2.0F;
    }

    protected float getTotal(float[] array) {
        float total = 0.0F;
        for (float v : array)
            total += v;
        return total;
    }

    protected float computeTextHeight() {
        float textSize = getTextSize();
        if (this.mText.length() < 1) {
            return (int) textSize;
        }
        return Math.max(textSize, getNumLines() * textSize);
    }

    protected float computeTextWidth() {
        float maxWidth = 0.0F;

        if (this.mText.length() > 0) {
            if (getNumLines() == 1) {
                maxWidth = getTextWidth(0, this.mText.length()) + this.mCursorWidth
                           + this.mCursorDistance;
            } else {
                int start = 0;
                for (int i = 0; i < this.linesBreak.size(); i++) {
                    int nextBreak = ((Integer) this.linesBreak.get(i)).intValue();
                    float real = getTextWidth(start, nextBreak) + this.mCursorWidth
                                 + this.mCursorDistance;
                    maxWidth = Math.max(maxWidth, real);
                    start = nextBreak + 1;
                }
            }
        }

        float result = Math.max(maxWidth, this.mMinWidth);

        return result;
    }

    protected float getTextWidth(int start, int stop) {
        float[] w = new float[stop - start];
        this.mPaint.getTextWidths(this.mText, start, stop, w);
        return getTotal(w);
    }

    protected void copyBounds(RectF rect) {
        rect.set(this.mBoundsF);
    }

    public void draw(Canvas canvas) {
        copyBounds(this.drawRect);

        if (this.mDebugPaint != null) {
            canvas.drawRect(this.drawRect, this.mDebugPaint);
        }

        int numLines = getNumLines();

        getFontMetrics(this.metrics);

        //FIXME FontMetrics坐标不做处理
        //metrics = new FontMetrics();
        float descent = this.metrics.descent + this.metrics.leading
                        - (this.metrics.bottom - this.metrics.descent);
        float ascent = this.metrics.ascent - (this.metrics.top - this.metrics.ascent);
        float top = this.drawRect.top;
        float left = this.drawRect.left;
        int start = 0;

        top += head_space;
        for (int i = 0; i < numLines; i++) {
            top -= ascent;
            int stop = ((Integer) this.linesBreak.get(i)).intValue();
            String text = this.mText.substring(start, stop);

            if ((!isTextHint()) && (this.mStrokeEnabled)) {
                canvas.drawText(text, left, top, this.mStrokePaint);
            }
            canvas.drawText(text, left, top, this.mPaint);

            if ((this.mEditing) && (i == numLines - 1)) {
                long now = System.currentTimeMillis();
                if (now - this.mNow > 400L) {
                    this.mShowCursor = (!this.mShowCursor);
                    this.mNow = now;
                }

                if (this.mShowCursor) {
                    getLineBounds(numLines - 1, this.lastRect);

                    float l = this.drawRect.left
                              + this.lastRect.right
                              + this.mCursorDistance
                              + ((isTextHint()) || (!this.mStrokeEnabled) ? 0.0F
                                  : this.mStrokePaint.getStrokeWidth() / 2.0F);
                    float t = top + ascent;
                    float r = this.drawRect.left
                              + this.lastRect.right
                              + this.mCursorWidth
                              + this.mCursorDistance
                              + ((isTextHint()) || (!this.mStrokeEnabled) ? 0.0F
                                  : this.mStrokePaint.getStrokeWidth() / 2.0F);
                    float b = top + this.metrics.descent;

                    canvas.drawRect(l, t, r, b, this.mCursorPaint);
                }
            }

            start = stop + 1;
            top += descent + line_space;
        }
    }

    private int line_space = 0;
    private int head_space = 0;
    public void setLineSpace(int lineSpace) {
        this.line_space = lineSpace;
    }

    public void setHeadSpace(int headSpace) {
        this.head_space = headSpace;
    }
    protected void getLineBounds(int line, Rect outBounds) {
        if (this.mText.length() > 0) {
            if (getNumLines() == 1) {
                this.mPaint.getTextBounds(this.mText, 0, this.mText.length(), outBounds);
                outBounds.left = 0;
            } else {
                this.mPaint.getTextBounds(this.mText,
                    ((Integer) this.linesBreak.get(line - 1)).intValue() + 1,
                    ((Integer) this.linesBreak.get(line)).intValue(), outBounds);
                outBounds.left = 0;
                outBounds.right = ((int) getTextWidth(
                    ((Integer) this.linesBreak.get(line - 1)).intValue() + 1,
                    ((Integer) this.linesBreak.get(line)).intValue()));
            }
        } else {
            this.mPaint.getTextBounds(this.mText, 0, this.mText.length(), outBounds);
            outBounds.left = 0;
            outBounds.right = 0;
        }
    }

    public int getNumLines() {
        return Math.max(this.linesBreak.size(), 1);
    }

    protected void onTextInvalidate() {

        if (isTextHint()) {
            this.mPaint.setAlpha(this.mPaintAlpha / 2);
            this.mStrokePaint.setAlpha(this.mStrokeAlpha / 2);
        } else {
            this.mPaint.setAlpha(this.mPaintAlpha);
            this.mStrokePaint.setAlpha(this.mStrokeAlpha);
        }

        this.linesBreak.clear();
        int start = 0;
        int last = -1;

        while ((last = this.mText.indexOf('\n', start)) > -1) {
            start = last + 1;
            this.linesBreak.add(Integer.valueOf(last));
        }
        this.linesBreak.add(Integer.valueOf(this.mText.length()));

        invalidateSize();

        if (this.mSizeChangeListener != null)
            this.mSizeChangeListener.onSizeChanged(this, this.mBoundsF.left, this.mBoundsF.top,
                this.mBoundsF.left + getIntrinsicWidth(), this.mBoundsF.top + getIntrinsicHeight());
    }

    public void setOnSizeChangeListener(EditableDrawable.OnSizeChange listener) {
        this.mSizeChangeListener = listener;
    }
}
