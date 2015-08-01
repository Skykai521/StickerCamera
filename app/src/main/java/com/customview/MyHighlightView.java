package com.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.customview.drawable.EditableDrawable;
import com.customview.drawable.FeatherDrawable;
import com.github.skykai.stickercamera.R;
import com.imagezoom.ImageViewTouch;
import com.stickercamera.App;
import com.stickercamera.app.camera.util.Point2D;
import com.stickercamera.app.camera.util.UIUtils;


public class MyHighlightView implements EditableDrawable.OnSizeChange {

    static final String LOG_TAG = "drawable-view";

    public static enum AlignModeV {
        Top, Bottom, Center
    };

    public interface OnDeleteClickListener {
        void onDeleteClick();
    }

    private int                   STATE_NONE                 = 1 << 0;
    private int                   STATE_SELECTED             = 1 << 1;
    private int                   STATE_FOCUSED              = 1 << 2;

    private OnDeleteClickListener mDeleteClickListener;

    public static final int       NONE                       = 1 << 0;                                     // 1
    public static final int       GROW_LEFT_EDGE             = 1 << 1;                                     // 2
    public static final int       GROW_RIGHT_EDGE            = 1 << 2;                                     // 4
    public static final int       GROW_TOP_EDGE              = 1 << 3;                                     // 8
    public static final int       GROW_BOTTOM_EDGE           = 1 << 4;                                     // 16
    public static final int       ROTATE                     = 1 << 5;                                     // 32
    public static final int       MOVE                       = 1 << 6;                                     // 64

    public static final int       GROW                       = GROW_TOP_EDGE | GROW_BOTTOM_EDGE
                                                               | GROW_LEFT_EDGE | GROW_RIGHT_EDGE;

    private static final float    HIT_TOLERANCE              = 40f;

    private boolean               mHidden;
    private int                   mMode;
    private int                   mState                     = STATE_NONE;
    private RectF                 mDrawRect;
    private final RectF           mTempRect                  = new RectF();
    private RectF                 mCropRect;
    private Matrix                mMatrix;
    private FeatherDrawable       mContent;
    private EditableDrawable      mEditableContent;
    private Drawable              mAnchorRotate;
    private Drawable              mAnchorDelete;
    private Drawable              mBackgroundDrawable;
    private int                   mAnchorRotateWidth;
    private int                   mAnchorRotateHeight;
    private int                   mAnchorDeleteHeight;
    private int                   mAnchorDeleteWidth;
    private int                   mResizeEdgeMode;

    private boolean               mRotateEnabled;
    private boolean               mScaleEnabled;
    private boolean               mMoveEnabled;

    private float                 mRotation                  = 0;
    private float                 mRatio                     = 1f;
    private Matrix                mRotateMatrix              = new Matrix();
    private final float           fpoints[]                  = new float[] { 0, 0 };

    private int                   mPadding                   = 0;
    private boolean               mShowAnchors               = true;
    private AlignModeV            mAlignVerticalMode         = AlignModeV.Center;
    private ImageViewTouch        mContext;

    private static final int[]    STATE_SET_NONE             = new int[] {};
    private static final int[]    STATE_SET_SELECTED         = new int[] { android.R.attr.state_selected };
    private static final int[]    STATE_SET_SELECTED_PRESSED = new int[] {
            android.R.attr.state_selected, android.R.attr.state_pressed };
    private static final int[]    STATE_SET_SELECTED_FOCUSED = new int[] { android.R.attr.state_focused };

    private final Paint outlinePaint = new Paint();
    private Path outlinePath;

    public void setMoveable(boolean moveable) {
        this.mMoveEnabled = moveable;
    }

    public void setScaleable(boolean scaleable) {
        this.mScaleEnabled = scaleable;
        if (scaleable) {
            mAnchorRotate = App.getApp().getResources().getDrawable(R.drawable.aviary_resize_knob);
        } else {
            mAnchorRotate = null;
        }
    }

    public void setDeleteable(boolean deleteable) {
        if (deleteable) {
            mAnchorDelete = App.getApp().getResources().getDrawable(R.drawable.aviary_delete_knob);
        } else {
            mAnchorDelete = null;
        }
    }

    public MyHighlightView(ImageView context, int styleId, FeatherDrawable content) {
        mContent = content;

        if (content instanceof EditableDrawable) {
            mEditableContent = (EditableDrawable) content;
            mEditableContent.setOnSizeChangeListener(this);
        } else {
            mEditableContent = null;
        }

        float minSize = -1f;

        Log.i(LOG_TAG, "DrawableHighlightView. styleId: " + styleId);
        mMoveEnabled = true;
        mRotateEnabled = true;
        mScaleEnabled = true;

        mAnchorRotate = App.getApp().getResources().getDrawable(R.drawable.aviary_resize_knob);
        mAnchorDelete = App.getApp().getResources().getDrawable(R.drawable.aviary_delete_knob);

        if (null != mAnchorRotate) {
            mAnchorRotateWidth = mAnchorRotate.getIntrinsicWidth() / 2;
            mAnchorRotateHeight = mAnchorRotate.getIntrinsicHeight() / 2;
        }

        if (null != mAnchorDelete) {
            mAnchorDeleteWidth = mAnchorDelete.getIntrinsicWidth() / 2;
            mAnchorDeleteHeight = mAnchorDelete.getIntrinsicHeight() / 2;
        }

        updateRatio();

        if (minSize > 0) {
            setMinSize(minSize);
        }
    }

    public void setAlignModeV(AlignModeV mode) {
        mAlignVerticalMode = mode;
    }

    protected RectF computeLayout() {
        return getDisplayRect(mMatrix, mCropRect);
    }

    public void dispose() {
        mDeleteClickListener = null;
        mContext = null;
        mContent = null;
        mEditableContent = null;
    }

    public void copyBounds(RectF outRect) {
        outRect.set(mDrawRect);
        outRect.inset(-mPadding, -mPadding);
    }

    public void draw(final Canvas canvas) {
        if (mHidden)
            return;

        copyBounds(mTempRect);

        final int saveCount = canvas.save();
        canvas.concat(mRotateMatrix);

        if (null != mBackgroundDrawable) {
            mBackgroundDrawable.setBounds((int) mTempRect.left, (int) mTempRect.top,
                (int) mTempRect.right, (int) mTempRect.bottom);
            mBackgroundDrawable.draw(canvas);
        }

        boolean is_selected = isSelected();
        boolean is_focused = isFocused();

        if (mEditableContent != null) {
            mEditableContent.setBounds(mDrawRect.left, mDrawRect.top, mDrawRect.right,
                mDrawRect.bottom);
        } else {
            mContent.setBounds((int) mDrawRect.left, (int) mDrawRect.top, (int) mDrawRect.right,
                (int) mDrawRect.bottom);
        }

        mContent.draw(canvas);

        if (is_selected || is_focused) {

            if (mShowAnchors) {
                //绘制边框
                outlinePath.reset();
                outlinePath.addRect(mTempRect, Path.Direction.CW);
                outlinePaint.setColor(Color.WHITE);
                outlinePaint.setStrokeWidth(App.getApp().dp2px(1));
                canvas.drawPath(outlinePath, outlinePaint);

                final int left = (int) (mTempRect.left);
                final int right = (int) (mTempRect.right);
                final int top = (int) (mTempRect.top);
                final int bottom = (int) (mTempRect.bottom);

                if (mAnchorRotate != null) {
                    mAnchorRotate.setBounds(right - mAnchorRotateWidth, bottom
                                                                        - mAnchorRotateHeight,
                        right + mAnchorRotateWidth, bottom + mAnchorRotateHeight);
                    mAnchorRotate.draw(canvas);
                }

                if (mAnchorDelete != null) {
                    mAnchorDelete.setBounds(left - mAnchorDeleteWidth, top - mAnchorDeleteHeight,
                        left + mAnchorDeleteWidth, top + mAnchorDeleteHeight);
                    mAnchorDelete.draw(canvas);
                }


            }
        }

        canvas.restoreToCount(saveCount);
    }

    public void showAnchors(boolean value) {
        mShowAnchors = value;
    }

    public void draw(final Canvas canvas, final Matrix source) {

        final Matrix matrix = new Matrix(source);
        matrix.invert(matrix);

        final int saveCount = canvas.save();
        canvas.concat(matrix);
        canvas.concat(mRotateMatrix);

        mContent.setBounds((int) mDrawRect.left, (int) mDrawRect.top, (int) mDrawRect.right,
            (int) mDrawRect.bottom);
        mContent.draw(canvas);

        canvas.restoreToCount(saveCount);
    }

    public Rect getCropRect() {
        return new Rect((int) mCropRect.left, (int) mCropRect.top, (int) mCropRect.right,
            (int) mCropRect.bottom);
    }

    public RectF getCropRectF() {
        return mCropRect;
    }

    public Matrix getCropRotationMatrix() {
        final Matrix m = new Matrix();
        m.postTranslate(-mCropRect.centerX(), -mCropRect.centerY());
        m.postRotate(mRotation);
        m.postTranslate(mCropRect.centerX(), mCropRect.centerY());
        return m;
    }

    public RectF getDisplayRect(final Matrix m, final RectF supportRect) {
        final RectF r = new RectF(supportRect);
        m.mapRect(r);
        return r;
    }

    public RectF getDisplayRectF() {
        final RectF r = new RectF(mDrawRect);
        mRotateMatrix.mapRect(r);
        return r;
    }

    public RectF getDrawRect() {
        return mDrawRect;
    }

    public int getHit(float x, float y) {
        final RectF rect = new RectF(mDrawRect);
        rect.inset(-mPadding, -mPadding);

        final float pts[] = new float[] { x, y };

        final Matrix rotateMatrix = new Matrix();
        rotateMatrix.postTranslate(-rect.centerX(), -rect.centerY());
        rotateMatrix.postRotate(-mRotation);
        rotateMatrix.postTranslate(rect.centerX(), rect.centerY());
        rotateMatrix.mapPoints(pts);

        x = pts[0];
        y = pts[1];

        int retval = NONE;
        final boolean verticalCheck = (y >= (rect.top - HIT_TOLERANCE))
                                      && (y < (rect.bottom + HIT_TOLERANCE));
        final boolean horizCheck = (x >= (rect.left - HIT_TOLERANCE))
                                   && (x < (rect.right + HIT_TOLERANCE));

        // if horizontal and vertical checks are good then
        // at least the move edge is selected
        if (verticalCheck && horizCheck) {
            retval = MOVE;
        }

        if (mScaleEnabled) {
            Log.d(LOG_TAG, "scale enabled");
            if ((Math.abs(rect.left - x) < HIT_TOLERANCE) && verticalCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_LEFT_EDGE)) {
                Log.d(LOG_TAG, "left");
                retval |= GROW_LEFT_EDGE;
            }
            if ((Math.abs(rect.right - x) < HIT_TOLERANCE) && verticalCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_RIGHT_EDGE)) {
                Log.d(LOG_TAG, "right");
                retval |= GROW_RIGHT_EDGE;
            }
            if ((Math.abs(rect.top - y) < HIT_TOLERANCE) && horizCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_TOP_EDGE)) {
                Log.d(LOG_TAG, "top");
                retval |= GROW_TOP_EDGE;
            }
            if ((Math.abs(rect.bottom - y) < HIT_TOLERANCE) && horizCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_BOTTOM_EDGE)) {
                Log.d(LOG_TAG, "bottom");
                retval |= GROW_BOTTOM_EDGE;
            }
        }

        if ((mRotateEnabled || mScaleEnabled) && (Math.abs(rect.right - x) < HIT_TOLERANCE)
            && (Math.abs(rect.bottom - y) < HIT_TOLERANCE) && verticalCheck && horizCheck) {
            retval = ROTATE;
        }

        if (mMoveEnabled && (retval == NONE) && rect.contains((int) x, (int) y)) {
            retval = MOVE;
        }

        Log.d(LOG_TAG, "retValue: " + retval);

        return retval;
    }

    public void onSingleTapConfirmed(float x, float y) {
        final RectF rect = new RectF(mDrawRect);
        rect.inset(-mPadding, -mPadding);

        final float pts[] = new float[] { x, y };

        final Matrix rotateMatrix = new Matrix();
        rotateMatrix.postTranslate(-rect.centerX(), -rect.centerY());
        rotateMatrix.postRotate(-mRotation);
        rotateMatrix.postTranslate(rect.centerX(), rect.centerY());
        rotateMatrix.mapPoints(pts);

        x = pts[0];
        y = pts[1];

        // mContext.invalidate();

        final boolean verticalCheck = (y >= (rect.top - HIT_TOLERANCE))
                                      && (y < (rect.bottom + HIT_TOLERANCE));
        final boolean horizCheck = (x >= (rect.left - HIT_TOLERANCE))
                                   && (x < (rect.right + HIT_TOLERANCE));

        if (mAnchorDelete != null) {
            if ((Math.abs(rect.left - x) < HIT_TOLERANCE)
                && (Math.abs(rect.top - y) < HIT_TOLERANCE) && verticalCheck && horizCheck) {
                if (mDeleteClickListener != null) {
                    mDeleteClickListener.onDeleteClick();
                }
            }
        }
    }

    RectF mInvalidateRectF = new RectF();
    Rect  mInvalidateRect  = new Rect();

    public Rect getInvalidationRect() {
        mInvalidateRectF.set(mDrawRect);
        mInvalidateRectF.inset(-mPadding, -mPadding);
        mRotateMatrix.mapRect(mInvalidateRectF);

        mInvalidateRect.set((int) mInvalidateRectF.left, (int) mInvalidateRectF.top,
            (int) mInvalidateRectF.right, (int) mInvalidateRectF.bottom);

        int w = Math.max(mAnchorRotateWidth, mAnchorDeleteWidth);
        int h = Math.max(mAnchorRotateHeight, mAnchorDeleteHeight);

        mInvalidateRect.inset(-w * 2, -h * 2);
        return mInvalidateRect;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public int getMode() {
        return mMode;
    }

    public float getRotation() {
        return mRotation;
    }

    public Matrix getRotationMatrix() {
        return mRotateMatrix;
    }

    protected void growBy(final float dx) {
        growBy(dx, dx / mRatio, true);
    }

    protected void growBy(final float dx, final float dy, boolean checkMinSize) {
        if (!mScaleEnabled)
            return;

        final RectF r = new RectF(mCropRect);

        if (mAlignVerticalMode == AlignModeV.Center) {
            r.inset(-dx, -dy);
        } else if (mAlignVerticalMode == AlignModeV.Top) {
            r.inset(-dx, 0);
            r.bottom += dy * 2;
        } else {
            r.inset(-dx, 0);
            r.top -= dy * 2;
        }

        RectF testRect = getDisplayRect(mMatrix, r);

        if (!mContent.validateSize(testRect) && checkMinSize) {
            return;
        }

        mCropRect.set(r);
        invalidate();
    }

    public void onMouseMove(int edge, MotionEvent event2, float dx, float dy) {
        if (edge == NONE) {
            return;
        }

        fpoints[0] = dx;
        fpoints[1] = dy;

        float xDelta;
        float yDelta;

        if (edge == MOVE) {
            moveBy(dx * (mCropRect.width() / mDrawRect.width()),
                dy * (mCropRect.height() / mDrawRect.height()));
        } else if (edge == ROTATE) {
            dx = fpoints[0];
            dy = fpoints[1];
            xDelta = dx * (mCropRect.width() / mDrawRect.width());
            yDelta = dy * (mCropRect.height() / mDrawRect.height());
            rotateBy(event2.getX(), event2.getY(), dx, dy);

            invalidate();
            // mContext.invalidate( getInvalidationRect() );
        } else {

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-mRotation);
            rotateMatrix.mapPoints(fpoints);
            dx = fpoints[0];
            dy = fpoints[1];

            if (((GROW_LEFT_EDGE | GROW_RIGHT_EDGE) & edge) == 0)
                dx = 0;
            if (((GROW_TOP_EDGE | GROW_BOTTOM_EDGE) & edge) == 0)
                dy = 0;

            xDelta = dx * (mCropRect.width() / mDrawRect.width());
            yDelta = dy * (mCropRect.height() / mDrawRect.height());

            boolean is_left = UIUtils.checkBits(edge, GROW_LEFT_EDGE);
            boolean is_top = UIUtils.checkBits(edge, GROW_TOP_EDGE);

            float delta;

            if (Math.abs(xDelta) >= Math.abs(yDelta)) {
                delta = xDelta;
                if (is_left) {
                    delta *= -1;
                }
            } else {
                delta = yDelta;
                if (is_top) {
                    delta *= -1;
                }
            }

            Log.d(LOG_TAG, "x: " + xDelta + ", y: " + yDelta + ", final: " + delta);

            growBy(delta);

            invalidate();
            // mContext.invalidate( getInvalidationRect() );
        }
    }

    void onMove(float dx, float dy) {
        moveBy(dx * (mCropRect.width() / mDrawRect.width()),
            dy * (mCropRect.height() / mDrawRect.height()));
    }

    public void invalidate() {
        mDrawRect = computeLayout(); // true
        Log.d(LOG_TAG, "computeLayout: " + mDrawRect);

        if (mDrawRect != null && mDrawRect.left > 1200) {
            Log.e(LOG_TAG, "computeLayout: " + mDrawRect);
        }
        mRotateMatrix.reset();
        mRotateMatrix.postTranslate(-mDrawRect.centerX(), -mDrawRect.centerY());
        mRotateMatrix.postRotate(mRotation);
        mRotateMatrix.postTranslate(mDrawRect.centerX(), mDrawRect.centerY());
    }

    void moveBy(final float dx, final float dy) {
        if (mMoveEnabled) {
            mCropRect.offset(dx, dy);
            invalidate();
        }
    }

    void rotateBy(final float dx, final float dy, float diffx, float diffy) {

        if (!mRotateEnabled && !mScaleEnabled)
            return;

        final float pt1[] = new float[] { mDrawRect.centerX(), mDrawRect.centerY() };
        final float pt2[] = new float[] { mDrawRect.right, mDrawRect.bottom };
        final float pt3[] = new float[] { dx, dy };

        final double angle1 = Point2D.angleBetweenPoints(pt2, pt1);
        final double angle2 = Point2D.angleBetweenPoints(pt3, pt1);

        if (mRotateEnabled) {
            mRotation = -(float) (angle2 - angle1);
        }

        if (mScaleEnabled) {

            final Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-mRotation);

            final float points[] = new float[] { diffx, diffy };
            rotateMatrix.mapPoints(points);

            diffx = points[0];
            diffy = points[1];

            final float xDelta = diffx * (mCropRect.width() / mDrawRect.width());
            final float yDelta = diffy * (mCropRect.height() / mDrawRect.height());

            final float pt4[] = new float[] { mDrawRect.right + xDelta, mDrawRect.bottom + yDelta };
            final double distance1 = Point2D.distance(pt1, pt2);
            final double distance2 = Point2D.distance(pt1, pt4);
            final float distance = (float) (distance2 - distance1);
            growBy(distance);
        }

    }

    void onRotateAndGrow(double angle, float scaleFactor) {

        if (!mRotateEnabled)
            mRotation -= (float) (angle);

        if (mRotateEnabled) {
            mRotation -= (float) (angle);
            growBy(scaleFactor * (mCropRect.width() / mDrawRect.width()));
        }

        invalidate();
    }

    public void setHidden(final boolean hidden) {
        mHidden = hidden;
    }

    public void setMinSize(final float size) {
        if (mRatio >= 1) {
            mContent.setMinSize(size, size / mRatio);
        } else {
            mContent.setMinSize(size * mRatio, size);
        }
    }

    public void setMode(final int mode) {
        Log.i(LOG_TAG, "setMode: " + mode);
        if (mode != mMode) {
            mMode = mode;
            updateDrawableState();
        }
    }

    public boolean isPressed() {
        return isSelected() && mMode != NONE;
    }

    protected void updateDrawableState() {

        if (null == mBackgroundDrawable)
            return;

        boolean is_selected = isSelected();
        boolean is_focused = isFocused();

        if (is_selected) {
            if (mMode == NONE) {
                if (is_focused) {
                    mBackgroundDrawable.setState(STATE_SET_SELECTED_FOCUSED);
                } else {
                    mBackgroundDrawable.setState(STATE_SET_SELECTED);
                }
            } else {
                mBackgroundDrawable.setState(STATE_SET_SELECTED_PRESSED);
            }

        } else {
            // normal state
            mBackgroundDrawable.setState(STATE_SET_NONE);
        }
    }

    public void setOnDeleteClickListener(final OnDeleteClickListener listener) {
        mDeleteClickListener = listener;
    }

    public void setSelected(final boolean selected) {
        Log.d(LOG_TAG, "setSelected: " + selected);
        boolean is_selected = isSelected();
        if (is_selected != selected) {
            mState ^= STATE_SELECTED;
            updateDrawableState();
        }
    }

    public boolean isSelected() {
        return (mState & STATE_SELECTED) == STATE_SELECTED;
    }

    public void setFocused(final boolean value) {
        Log.i(LOG_TAG, "setFocused: " + value);
        boolean is_focused = isFocused();
        if (is_focused != value) {
            mState ^= STATE_FOCUSED;

            if (null != mEditableContent) {
                if (value) {
                    mEditableContent.beginEdit();
                } else {
                    mEditableContent.endEdit();
                }
            }
            updateDrawableState();
        }
    }

    public boolean isFocused() {
        return (mState & STATE_FOCUSED) == STATE_FOCUSED;
    }

    public void setup(final Context context, final Matrix m, final Rect imageRect,
                      final RectF cropRect, final boolean maintainAspectRatio) {
        mMatrix = new Matrix(m);
        mRotation = 0;
        mRotateMatrix = new Matrix();
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setAntiAlias(true);
        outlinePath = new Path();
        mCropRect = cropRect;
        setMode(NONE);
        invalidate();
    }

    public void setup(final Context context, final Matrix m, final Rect imageRect,
                      final RectF cropRect, final boolean maintainAspectRatio, final float rotation) {
        mMatrix = new Matrix(m);
        mRotation = rotation;
        mRotateMatrix = new Matrix();
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setAntiAlias(true);
        outlinePath = new Path();
        mCropRect = cropRect;
        setMode(NONE);
        invalidate();
    }

    public void update(final Matrix imageMatrix, final Rect imageRect) {
        setMode(NONE);
        mMatrix = new Matrix(imageMatrix);
        mRotation = 0;
        mRotateMatrix = new Matrix();
        invalidate();
    }

    public FeatherDrawable getContent() {
        return mContent;
    }

    private void updateRatio() {
        final float w = mContent.getCurrentWidth();
        final float h = mContent.getCurrentHeight();
        mRatio = w / h;
    }

    public boolean forceUpdate() {
        Log.i(LOG_TAG, "forceUpdate");

        RectF cropRect = getCropRectF();
        RectF drawRect = getDrawRect();

        if (mEditableContent != null) {

            final float textWidth = mContent.getCurrentWidth();
            final float textHeight = mContent.getCurrentHeight();

            updateRatio();

            RectF textRect = new RectF(cropRect);
            getMatrix().mapRect(textRect);

            float dx = textWidth - textRect.width();
            float dy = textHeight - textRect.height();

            float[] fpoints = new float[] { dx, dy };

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-mRotation);

            dx = fpoints[0];
            dy = fpoints[1];

            float xDelta = dx * (cropRect.width() / drawRect.width());
            float yDelta = dy * (cropRect.height() / drawRect.height());

            if (xDelta != 0 || yDelta != 0) {
                growBy(xDelta / 2, yDelta / 2, false);
            }

            invalidate();
            return true;
        }
        return false;
    }

    public void setPadding(int value) {
        mPadding = value;
    }

    @Override
    public void onSizeChanged(EditableDrawable content, float left, float top, float right,
                              float bottom) {
        Log.i(LOG_TAG, "onSizeChanged: " + left + ", " + top + ", " + right + ", " + bottom);
        if (content.equals(mEditableContent) && null != mContext) {

            if (mDrawRect.left != left || mDrawRect.top != top || mDrawRect.right != right
                || mDrawRect.bottom != bottom) {
                if (forceUpdate()) {
                    mContext.invalidate(getInvalidationRect());
                } else {
                    mContext.postInvalidate();
                }
            }
        }
    }
}
