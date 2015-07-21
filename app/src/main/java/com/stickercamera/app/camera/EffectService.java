package com.stickercamera.app.camera;

import com.stickercamera.app.camera.effect.FilterEffect;
import com.stickercamera.app.camera.util.GPUImageFilterTools;

import java.util.ArrayList;
import java.util.List;


public class EffectService {

    private static EffectService mInstance;

    public static EffectService getInst() {
        if (mInstance == null) {
            synchronized (EffectService.class) {
                if (mInstance == null)
                    mInstance = new EffectService();
            }
        }
        return mInstance;
    }

    private EffectService() {
    }

    public List<FilterEffect> getLocalFilters() {
        List<FilterEffect> filters = new ArrayList<FilterEffect>();
        filters.add(new FilterEffect("原始", GPUImageFilterTools.FilterType.NORMAL, 0));

        filters.add(new FilterEffect("暧昧", GPUImageFilterTools.FilterType.ACV_AIMEI, 0));
        filters.add(new FilterEffect("淡蓝", GPUImageFilterTools.FilterType.ACV_DANLAN, 0));
        filters.add(new FilterEffect("蛋黄", GPUImageFilterTools.FilterType.ACV_DANHUANG, 0));
        filters.add(new FilterEffect("复古", GPUImageFilterTools.FilterType.ACV_FUGU, 0));
        filters.add(new FilterEffect("高冷", GPUImageFilterTools.FilterType.ACV_GAOLENG, 0));
        filters.add(new FilterEffect("怀旧", GPUImageFilterTools.FilterType.ACV_HUAIJIU, 0));
        filters.add(new FilterEffect("胶片", GPUImageFilterTools.FilterType.ACV_JIAOPIAN, 0));
        filters.add(new FilterEffect("可爱", GPUImageFilterTools.FilterType.ACV_KEAI, 0));
        filters.add(new FilterEffect("落寞", GPUImageFilterTools.FilterType.ACV_LOMO, 0));
        filters.add(new FilterEffect("加强", GPUImageFilterTools.FilterType.ACV_MORENJIAQIANG, 0));
        filters.add(new FilterEffect("暖心", GPUImageFilterTools.FilterType.ACV_NUANXIN, 0));
        filters.add(new FilterEffect("清新", GPUImageFilterTools.FilterType.ACV_QINGXIN, 0));
        filters.add(new FilterEffect("日系", GPUImageFilterTools.FilterType.ACV_RIXI, 0));
        filters.add(new FilterEffect("温暖", GPUImageFilterTools.FilterType.ACV_WENNUAN, 0));

        return filters;
    }

}
