package com.stickercamera.app.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.common.util.ImageUtils;
import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.customview.drawable.StickerDrawable;
import com.github.skykai.stickercamera.R;
import com.imagezoom.ImageViewTouch;
import com.stickercamera.App;
import com.stickercamera.AppConstants;
import com.stickercamera.app.model.Addon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sky on 15/7/6.
 */
public class EffectUtil {

    public static List<Addon> addonList                 = new ArrayList<Addon>();
    private static List<MyHighlightView> hightlistViews = new CopyOnWriteArrayList<MyHighlightView>();

    static {
        addonList.add(new Addon(R.drawable.sticker1));
        addonList.add(new Addon(R.drawable.sticker2));
        addonList.add(new Addon(R.drawable.sticker3));
        addonList.add(new Addon(R.drawable.sticker4));
        addonList.add(new Addon(R.drawable.sticker5));
        addonList.add(new Addon(R.drawable.sticker6));
        addonList.add(new Addon(R.drawable.sticker7));
        addonList.add(new Addon(R.drawable.sticker8));
    }

    public static void clear() {
        hightlistViews.clear();
    }

    //删除贴纸的回调接口
    public static interface StickerCallback {
        public void onRemoveSticker(Addon sticker);
    }

    //添加贴纸
    public static MyHighlightView addStickerImage(final ImageViewTouch processImage,
                                                  Context context, final Addon sticker,
                                                  final StickerCallback callback) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), sticker.getId());
        if (bitmap == null) {
            return null;
        }
        StickerDrawable drawable = new StickerDrawable(context.getResources(), bitmap);
        drawable.setAntiAlias(true);
        drawable.setMinSize(30, 30);

        final MyHighlightView hv = new MyHighlightView(processImage, R.style.AppTheme, drawable);
        //设置贴纸padding
        hv.setPadding(10);
        hv.setOnDeleteClickListener(new MyHighlightView.OnDeleteClickListener() {

            @Override
            public void onDeleteClick() {
                ((MyImageViewDrawableOverlay) processImage).removeHightlightView(hv);
                hightlistViews.remove(hv);
                ((MyImageViewDrawableOverlay) processImage).invalidate();
                callback.onRemoveSticker(sticker);
            }
        });

        Matrix mImageMatrix = processImage.getImageViewMatrix();

        int cropWidth, cropHeight;
        int x, y;

        final int width = processImage.getWidth();
        final int height = processImage.getHeight();

        // width/height of the sticker
        cropWidth = (int) drawable.getCurrentWidth();
        cropHeight = (int) drawable.getCurrentHeight();

        final int cropSize = Math.max(cropWidth, cropHeight);
        final int screenSize = Math.min(processImage.getWidth(), processImage.getHeight());
        RectF positionRect = null;
        if (cropSize > screenSize) {
            float ratio;
            float widthRatio = (float) processImage.getWidth() / cropWidth;
            float heightRatio = (float) processImage.getHeight() / cropHeight;

            if (widthRatio < heightRatio) {
                ratio = widthRatio;
            } else {
                ratio = heightRatio;
            }

            cropWidth = (int) ((float) cropWidth * (ratio / 2));
            cropHeight = (int) ((float) cropHeight * (ratio / 2));

            int w = processImage.getWidth();
            int h = processImage.getHeight();
            positionRect = new RectF(w / 2 - cropWidth / 2, h / 2 - cropHeight / 2,
                    w / 2 + cropWidth / 2, h / 2 + cropHeight / 2);

            positionRect.inset((positionRect.width() - cropWidth) / 2,
                    (positionRect.height() - cropHeight) / 2);
        }

        if (positionRect != null) {
            x = (int) positionRect.left;
            y = (int) positionRect.top;

        } else {
            x = (width - cropWidth) / 2;
            y = (height - cropHeight) / 2;
        }

        Matrix matrix = new Matrix(mImageMatrix);
        matrix.invert(matrix);

        float[] pts = new float[] { x, y, x + cropWidth, y + cropHeight };
        MatrixUtils.mapPoints(matrix, pts);

        RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
        Rect imageRect = new Rect(0, 0, width, height);

        hv.setup(context, mImageMatrix, imageRect, cropRect, false);

        ((MyImageViewDrawableOverlay) processImage).addHighlightView(hv);
        ((MyImageViewDrawableOverlay) processImage).setSelectedHighlightView(hv);
        hightlistViews.add(hv);
        return hv;
    }


    //----添加标签-----
    public static void addLabelEditable(MyImageViewDrawableOverlay overlay, ViewGroup container,
                                        LabelView label, int left, int top) {
        addLabel(container, label, left, top);
        addLabel2Overlay(overlay, label);
    }

    private static void addLabel(ViewGroup container, LabelView label, int left, int top) {
        label.addTo(container, left, top);
    }

    public static void removeLabelEditable(MyImageViewDrawableOverlay overlay, ViewGroup container,
                                           LabelView label) {
        container.removeView(label);
        overlay.removeLabel(label);
    }

    public static int getStandDis(float realDis, float baseWidth) {
        float imageWidth = baseWidth <= 0 ? App.getApp().getScreenWidth() : baseWidth;
        float radio = AppConstants.DEFAULT_PIXEL / imageWidth;
        return (int) (radio * realDis);
    }

    public static int getRealDis(float standardDis, float baseWidth) {
        float imageWidth = baseWidth <= 0 ? App.getApp().getScreenWidth() : baseWidth;
        float radio = imageWidth / AppConstants.DEFAULT_PIXEL;
        return (int) (radio * standardDis);
    }

    /**
     * 使标签在Overlay上可以移动
     * @param overlay
     * @param label
     */
    private static void addLabel2Overlay(final MyImageViewDrawableOverlay overlay,
                                         final LabelView label) {
        //添加事件，触摸生效
        overlay.addLabel(label);
        label.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 手指按下时
                        overlay.setCurrentLabel(label, event.getRawX(), event.getRawY());
                        return false;
                    default:
                        return false;
                }
            }
        });
    }


    //添加水印
    public static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage) {
        for (MyHighlightView view : hightlistViews) {
            applyOnSave(mCanvas, processImage, view);
        }
    }

    private static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage,MyHighlightView view) {

        if (view != null && view.getContent() instanceof StickerDrawable) {

            final StickerDrawable stickerDrawable = ((StickerDrawable) view.getContent());
            RectF cropRect = view.getCropRectF();
            Rect rect = new Rect((int) cropRect.left, (int) cropRect.top, (int) cropRect.right,
                    (int) cropRect.bottom);

            Matrix rotateMatrix = view.getCropRotationMatrix();
            Matrix matrix = new Matrix(processImage.getImageMatrix());
            if (!matrix.invert(matrix)) {
            }
            int saveCount = mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
            mCanvas.concat(rotateMatrix);

            stickerDrawable.setDropShadow(false);
            view.getContent().setBounds(rect);
            view.getContent().draw(mCanvas);
            mCanvas.restoreToCount(saveCount);
        }
    }

}
