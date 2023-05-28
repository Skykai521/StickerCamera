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
package com.stickercamera.app.camera.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import com.stickercamera.app.camera.util.CameraHelper.CameraHelperImpl;
import com.stickercamera.app.camera.util.CameraHelper.CameraInfo2;

public class CameraHelperBase implements CameraHelperImpl {

    private final Context mContext;

    public CameraHelperBase(final Context context) {
        mContext = context;
    }

    @Override
    public int getNumberOfCameras() {
        return hasCameraSupport() ? 1 : 0;
    }

    @Override
    public Camera openCamera(final int id) {
        return Camera.open();
    }

    @Override
    public Camera openDefaultCamera() {
        return Camera.open();
    }

    @Override
    public boolean hasCamera(final int facing) {
        if (facing == CameraInfo.CAMERA_FACING_BACK) {
            return hasCameraSupport();
        }
        return false;
    }

    @Override
    public Camera openCameraFacing(final int facing) {
        if (facing == CameraInfo.CAMERA_FACING_BACK) {
            return Camera.open();
        }
        return null;
    }

    @Override
    public void getCameraInfo(final int cameraId, final CameraInfo2 cameraInfo) {
        cameraInfo.facing = CameraInfo.CAMERA_FACING_BACK;
        cameraInfo.orientation = 90;
    }

    private boolean hasCameraSupport() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
