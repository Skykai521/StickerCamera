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

import com.github.skykai.stickercamera.R;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter;

/**
 * 滤镜工厂。本 app 仅使用「原始」滤镜与 res/raw 下的 .acv 色调曲线滤镜
 * (见 {@link com.stickercamera.app.camera.EffectService})，因此这里只保留
 * NORMAL + ACV_* 两类。
 */
public class GPUImageFilterTools {

    public static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
        switch (type) {
            case NORMAL:
                return new GPUImageFilter();
            case ACV_AIMEI:
                return createCurveFilter(context, R.raw.aimei);
            case ACV_DANLAN:
                return createCurveFilter(context, R.raw.danlan);
            case ACV_DANHUANG:
                return createCurveFilter(context, R.raw.danhuang);
            case ACV_FUGU:
                return createCurveFilter(context, R.raw.fugu);
            case ACV_GAOLENG:
                return createCurveFilter(context, R.raw.gaoleng);
            case ACV_HUAIJIU:
                return createCurveFilter(context, R.raw.huaijiu);
            case ACV_JIAOPIAN:
                return createCurveFilter(context, R.raw.jiaopian);
            case ACV_KEAI:
                return createCurveFilter(context, R.raw.keai);
            case ACV_LOMO:
                return createCurveFilter(context, R.raw.lomo);
            case ACV_MORENJIAQIANG:
                return createCurveFilter(context, R.raw.morenjiaqiang);
            case ACV_NUANXIN:
                return createCurveFilter(context, R.raw.nuanxin);
            case ACV_QINGXIN:
                return createCurveFilter(context, R.raw.qingxin);
            case ACV_RIXI:
                return createCurveFilter(context, R.raw.rixi);
            case ACV_WENNUAN:
                return createCurveFilter(context, R.raw.wennuan);
            default:
                throw new IllegalStateException("No filter of that type!");
        }
    }

    private static GPUImageToneCurveFilter createCurveFilter(final Context context, final int rawResId) {
        GPUImageToneCurveFilter curveFilter = new GPUImageToneCurveFilter();
        curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(rawResId));
        return curveFilter;
    }

    public enum FilterType {
        NORMAL, ACV_AIMEI, ACV_DANLAN, ACV_DANHUANG, ACV_FUGU, ACV_GAOLENG, ACV_HUAIJIU,
        ACV_JIAOPIAN, ACV_KEAI, ACV_LOMO, ACV_MORENJIAQIANG, ACV_NUANXIN, ACV_QINGXIN,
        ACV_RIXI, ACV_WENNUAN
    }
}
