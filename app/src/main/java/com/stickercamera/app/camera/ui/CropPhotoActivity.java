package com.stickercamera.app.camera.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.common.util.FileUtils;
import com.common.util.IOUtil;
import com.common.util.ImageUtils;
import com.github.skykai.stickercamera.R;
import com.imagezoom.ImageViewTouch;
import com.stickercamera.App;
import com.stickercamera.app.camera.CameraBaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * 裁剪图片界面
 * Created by sky on 2015/7/8.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class CropPhotoActivity extends CameraBaseActivity {

    private static final boolean IN_MEMORY_CROP = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1;
    private Uri fileUri;
    private Bitmap oriBitmap;
    private int initWidth, initHeight;
    private static final int MAX_WRAP_SIZE  = 2048;

    @BindView(R.id.crop_image)
    ImageViewTouch cropImage;
    @BindView(R.id.draw_area)
    ViewGroup drawArea;
    @BindView(R.id.wrap_image)
    View wrapImage;
    @BindView(R.id.btn_crop_type)
    View btnCropType;
    @BindView(R.id.image_center)
    ImageView imageCenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 显示界面
        setContentView(R.layout.activity_new_crop);
        ButterKnife.bind(this);
        fileUri = getIntent().getData();
        initView();
        initEvent();
    }

    private void initEvent() {
        btnCropType.setOnClickListener(v -> {
            if (cropImage.getVisibility() == View.VISIBLE) {
                btnCropType.setSelected(true);
                cropImage.setVisibility(View.GONE);
                wrapImage.setVisibility(View.VISIBLE);
            } else {
                btnCropType.setSelected(false);
                cropImage.setVisibility(View.VISIBLE);
                wrapImage.setVisibility(View.GONE);
            }
        });
        imageCenter.setOnClickListener(v -> wrapImage.setSelected(!wrapImage.isSelected()));
        findViewById(R.id.cancel).setOnClickListener(v -> finish());
        findViewById(R.id.picked).setOnClickListener(v -> {
            showProgressDialog("图片处理中...");
            new Thread() {
                public void run() {
                    if (btnCropType.isSelected()) {
                        wrapImage();
                    } else {
                        cropImage();
                    }
                    dismissProgressDialog();
                };
            }.start();
        });
    }


    protected void wrapImage() {
        int width = initWidth > initHeight ? initWidth : initHeight;
        int imageSize = width < MAX_WRAP_SIZE ? width : MAX_WRAP_SIZE;

        int move =  (int)((initHeight - initWidth) / 2 / (float)width * (float)imageSize);
        int moveX = initWidth < initHeight ? move : 0;
        int moveY = initHeight < initWidth ? -move : 0;
        Bitmap croppedImage = null;
        try {
            croppedImage = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(croppedImage);
            Paint p = new Paint();
            p.setColor(wrapImage.isSelected() ? Color.BLACK : Color.WHITE);
            canvas.drawRect(0, 0, imageSize, imageSize, p);
            Matrix matrix = new Matrix();
            matrix.postScale((float) imageSize / (float) width, (float) imageSize / (float) width);
            matrix.postTranslate(moveX, moveY);
            canvas.drawBitmap(oriBitmap, matrix, null);
        } catch (OutOfMemoryError e) {
            Log.e("OOM cropping image: " + e.getMessage(), e.toString());
            System.gc();
        }
        saveImageToCache(croppedImage);
    }

    private void initView() {
        drawArea.getLayoutParams().height = App.getApp().getScreenWidth();
        InputStream inputStream = null;
        try {
            //得到图片宽高比
            double rate = ImageUtils.getImageRadio(getContentResolver(), fileUri);
            oriBitmap = ImageUtils.decodeBitmapWithOrientationMax(fileUri.getPath(), App.getApp().getScreenWidth(), App.getApp().getScreenHeight());

            initWidth = oriBitmap.getWidth();
            initHeight = oriBitmap.getHeight();

            cropImage.setImageBitmap(oriBitmap, new Matrix(), (float) rate, 10);
            imageCenter.setImageBitmap(oriBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(inputStream);
        }
    }

    private void cropImage() {
        Bitmap croppedImage;
        if (IN_MEMORY_CROP) {
            croppedImage = inMemoryCrop(cropImage);
        } else {
            try {
                croppedImage = decodeRegionCrop(cropImage);
            } catch (IllegalArgumentException e) {
                croppedImage = inMemoryCrop(cropImage);
            }
        }
        saveImageToCache(croppedImage);
    }

    private void saveImageToCache(Bitmap croppedImage) {
        if (croppedImage != null) {
            try {
                ImageUtils.saveToFile(FileUtils.getInst().getCacheDir() + "/croppedcache",
                        false, croppedImage);
                Intent i = new Intent();
                i.setData(Uri.parse("file://" + FileUtils.getInst().getCacheDir()
                        + "/croppedcache"));
                setResult(RESULT_OK, i);
                dismissProgressDialog();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                toast("裁剪图片异常，请稍后重试", Toast.LENGTH_LONG);
            }
        }
    }

    @TargetApi(10)
    private Bitmap decodeRegionCrop(ImageViewTouch cropImage) {
        int width = initWidth > initHeight ? initHeight : initWidth;
        int screenWidth = App.getApp().getScreenWidth();
        float scale = cropImage.getScale() / getImageRadio();
        RectF rectf = cropImage.getBitmapRect();
        int left = -(int) (rectf.left * width / screenWidth / scale);
        int top = -(int) (rectf.top * width / screenWidth / scale);
        int right = left + (int) (width / scale);
        int bottom = top + (int) (width / scale);
        Rect rect = new Rect(left, top, right, bottom);
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oriBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            is = new ByteArrayInputStream(baos.toByteArray());
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
        } catch (Throwable e) {

        } finally {
            IOUtil.closeStream(is);
        }
        return croppedImage;
    }

    private float getImageRadio() {
        return Math.max((float) initWidth, (float) initHeight)
                / Math.min((float) initWidth, (float) initHeight);
    }

    private Bitmap inMemoryCrop(ImageViewTouch cropImage) {
        int width = initWidth > initHeight ? initHeight : initWidth;
        int screenWidth = App.getApp().getScreenWidth();
        System.gc();
        Bitmap croppedImage = null;
        try {
            croppedImage = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            float scale = cropImage.getScale();
            RectF srcRect = cropImage.getBitmapRect();
            Matrix matrix = new Matrix();

            matrix.postScale(scale / getImageRadio(), scale / getImageRadio());
            matrix.postTranslate(srcRect.left * width / screenWidth, srcRect.top * width
                    / screenWidth);
            //matrix.mapRect(srcRect);
            canvas.drawBitmap(oriBitmap, matrix, null);
        } catch (OutOfMemoryError e) {
            Log.e("OOM cropping image: " + e.getMessage(), e.toString());
            System.gc();
        }
        return croppedImage;
    }


}
