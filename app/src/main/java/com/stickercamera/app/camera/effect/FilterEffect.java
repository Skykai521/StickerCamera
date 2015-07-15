/**
 * Copyright (C) 2014 xiaobudian Inc.
 */
package com.stickercamera.app.camera.effect;


import com.stickercamera.app.camera.util.GPUImageFilterTools;

/**
 * @author tongqian.ni
 *
 */
public class FilterEffect  {

    private String     title;
    private GPUImageFilterTools.FilterType type;
    private int        degree;

    /**
     * @param title
     * @param uri
     */
    public FilterEffect(String title, GPUImageFilterTools.FilterType type, int degree) {
        this.type = type;
        this.degree = degree;
        this.title = title;
    }


    public GPUImageFilterTools.FilterType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getDegree() {
        return degree;
    }

}
