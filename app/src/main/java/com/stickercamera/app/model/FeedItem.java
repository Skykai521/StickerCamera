package com.stickercamera.app.model;

import java.util.List;

/**
 * 图片Module
 * Created by sky on 15/7/18.
 */
public class FeedItem {

    private String imgPath;

    private List<TagItem> tagList;

    public FeedItem() {
    }

    public FeedItem(List<TagItem> tagList, String imgPath) {
        this.imgPath = imgPath;
        this.tagList = tagList;
    }

    public List<TagItem> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagItem> tagList) {
        this.tagList = tagList;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
