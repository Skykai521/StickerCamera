/**
 * Copyright (C) 2014 xiaobudian Inc.
 */
package com.stickercamera.app.model;

import com.alibaba.fastjson.JSON;
import com.common.util.StringUtils;

import java.util.List;

/**
 * @author tongqian.ni
 *
 */
public class Addon  {
    private int    dbid;
    private int    id;
    private int    packageId;
    private String author;
    private int    category;       //0是水印 1是贴纸
    private String desc;
    private String size;
    private String icon;
    private String sampleImage;
    private String layout;
    private String appliedTimes;
    private String createDate;
    private String maxAppliedTimes;
    private String filter;

    //JSON用到
    public Addon() {

    }

    public Addon(int id, int packageId, String icon, String sampleImg) {
        this.id = packageId * 500 + id;
        this.packageId = packageId;
        this.icon = icon;
        this.sampleImage = sampleImg;
    }

    public int getDbid() {
        return dbid;
    }

    public void setDbid(int dbid) {
        this.dbid = dbid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSampleImage() {
        return sampleImage;
    }

    public void setSampleImage(String sampleImage) {
        this.sampleImage = sampleImage;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getLayout() {
        //        if ("0".equals(category)) {
        //            return "{icon:\"http://matcha-resource.qiniudn.com/icon_46iweekly_03.png\",sampleImage:\"http://matcha-resource.qiniudn.com/r_46iweekly_03_s1.png\",layout:[{\"category\":\"img\",\"type\":\"sticker\",\"fileName\":\"http://matcha-resource.qiniudn.com/r_45qs_01_s1.png\",\"position\":{\"top\":\"110\",\"left\":\"220\"}},{\"category\":\"label\",\"type\":\"poi\",\"font\":{\"family\":\"\",\"size\":\"44\",\"color\":\"#FFFFFF\"},\"vertical\":\"true\",\"position\":{\"bottom\":\"35\",\"right\":\"130\"},\"placeholder\":\"关于美食的评”\"}]}";
        //        }

        return layout.replaceAll("ture", "true");
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getAppliedTimes() {
        return appliedTimes;
    }

    public void setAppliedTimes(String appliedTimes) {
        this.appliedTimes = appliedTimes;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getMaxAppliedTimes() {
        return maxAppliedTimes;
    }

    public void setMaxAppliedTimes(String maxAppliedTimes) {
        this.maxAppliedTimes = maxAppliedTimes;
    }

    /************************水印用到的***************************/
    private boolean      isMall;
    private boolean      isOri;
    private List<Layout> layoutObj;

    public boolean isMall() {
        return isMall;
    }

    public void setMall(boolean isMall) {
        this.isMall = isMall;
    }

    public boolean isOri() {
        return isOri;
    }

    public void setOri(boolean isOri) {
        this.isOri = isOri;
    }

    public List<Layout> getLayoutObj() {
        if (layoutObj == null && StringUtils.isNotEmpty(getLayout())) {
            layoutObj = JSON.parseArray(getLayout(), Layout.class);
        }
        return layoutObj;
    }

    public void setLayoutObj(List<Layout> layoutObj) {
        this.layoutObj = layoutObj;
    }
}
