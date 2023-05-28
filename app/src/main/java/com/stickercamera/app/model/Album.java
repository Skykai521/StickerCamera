package com.stickercamera.app.model;

import com.common.util.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author tongqian.ni
 */
public class Album implements Serializable {

    private static final long serialVersionUID = 5702699517846159671L;

    private String albumUri;

    private String title;

    private ArrayList<PhotoItem> photos;

    public Album(String title, String uri, ArrayList<PhotoItem> photos) {
        this.title = title;
        this.albumUri = uri;
        this.photos = photos;
    }

    public String getAlbumUri() {
        return albumUri;
    }

    public void setAlbumUri(String albumUri) {
        this.albumUri = albumUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<PhotoItem> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoItem> photos) {
        this.photos = photos;
    }

    @Override
    public int hashCode() {
        if (albumUri == null) {
            return super.hashCode();
        } else {
            return albumUri.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof Album)) {
            return StringUtils.equals(albumUri, ((Album) o).getAlbumUri());
        }
        return false;
    }
}
