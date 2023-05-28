/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;

public class GPUImage3x3TextureSamplingFilter extends GPUImageFilter {

    public static final String THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER = "" + "attribute vec4 position;\n" + "attribute vec4 inputTextureCoordinate;\n" + "\n" + "uniform highp float texelWidth; \n" + "uniform highp float texelHeight; \n" + "\n" + "varying vec2 textureCoordinate;\n" + "varying vec2 leftTextureCoordinate;\n" + "varying vec2 rightTextureCoordinate;\n" + "\n" + "varying vec2 topTextureCoordinate;\n" + "varying vec2 topLeftTextureCoordinate;\n" + "varying vec2 topRightTextureCoordinate;\n" + "\n" + "varying vec2 bottomTextureCoordinate;\n" + "varying vec2 bottomLeftTextureCoordinate;\n" + "varying vec2 bottomRightTextureCoordinate;\n" + "\n" + "void main()\n" + "{\n" + "    gl_Position = position;\n" + "\n" + "    vec2 widthStep = vec2(texelWidth, 0.0);\n" + "    vec2 heightStep = vec2(0.0, texelHeight);\n" + "    vec2 widthHeightStep = vec2(texelWidth, texelHeight);\n" + "    vec2 widthNegativeHeightStep = vec2(texelWidth, -texelHeight);\n" + "\n" + "    textureCoordinate = inputTextureCoordinate.xy;\n" + "    leftTextureCoordinate = inputTextureCoordinate.xy - widthStep;\n" + "    rightTextureCoordinate = inputTextureCoordinate.xy + widthStep;\n" + "\n" + "    topTextureCoordinate = inputTextureCoordinate.xy - heightStep;\n" + "    topLeftTextureCoordinate = inputTextureCoordinate.xy - widthHeightStep;\n" + "    topRightTextureCoordinate = inputTextureCoordinate.xy + widthNegativeHeightStep;\n" + "\n" + "    bottomTextureCoordinate = inputTextureCoordinate.xy + heightStep;\n" + "    bottomLeftTextureCoordinate = inputTextureCoordinate.xy - widthNegativeHeightStep;\n" + "    bottomRightTextureCoordinate = inputTextureCoordinate.xy + widthHeightStep;\n" + "}";

    private int mUniformTexelWidthLocation;

    private int mUniformTexelHeightLocation;

    private boolean mHasOverriddenImageSizeFactor = false;

    private float mTexelWidth;

    private float mTexelHeight;

    private float mLineSize = 1.0f;

    public GPUImage3x3TextureSamplingFilter() {
        this(NO_FILTER_VERTEX_SHADER);
    }

    public GPUImage3x3TextureSamplingFilter(final String fragmentShader) {
        super(THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER, fragmentShader);
    }

    @Override
    public void onInit() {
        super.onInit();
        mUniformTexelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        mUniformTexelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
        if (mTexelWidth != 0) {
            updateTexelValues();
        }
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        if (!mHasOverriddenImageSizeFactor) {
            setLineSize(mLineSize);
        }
    }

    public void setTexelWidth(final float texelWidth) {
        mHasOverriddenImageSizeFactor = true;
        mTexelWidth = texelWidth;
        setFloat(mUniformTexelWidthLocation, texelWidth);
    }

    public void setTexelHeight(final float texelHeight) {
        mHasOverriddenImageSizeFactor = true;
        mTexelHeight = texelHeight;
        setFloat(mUniformTexelHeightLocation, texelHeight);
    }

    public void setLineSize(final float size) {
        mLineSize = size;
        mTexelWidth = size / getOutputWidth();
        mTexelHeight = size / getOutputHeight();
        updateTexelValues();
    }

    private void updateTexelValues() {
        setFloat(mUniformTexelWidthLocation, mTexelWidth);
        setFloat(mUniformTexelHeightLocation, mTexelHeight);
    }
}
