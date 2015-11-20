package com.stickercamera.app.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.common.util.DistanceUtil;
import com.common.util.ImageLoaderUtils;
import com.github.skykai.stickercamera.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stickercamera.app.model.PhotoItem;
import com.tickercamera.ImageLoaderUtils.ImageInternalFetcher;

import java.util.List;

/**
 * @author tongqian.ni
 *
 */
public class GalleryAdapter extends BaseAdapter {

    private Context             mContext;
    private List<PhotoItem>     values;
    public static GalleryHolder holder;

    /**
     * @param albumActivity
     * @param values
     */
    public GalleryAdapter(Context context, List<PhotoItem> values) {
        this.mContext = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GalleryHolder holder;
        int width = DistanceUtil.getCameraAlbumWidth();
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_gallery, null);
            holder = new GalleryHolder();
            holder.sample = (ImageView) convertView.findViewById(R.id.gallery_sample_image);
            holder.sample.setLayoutParams(new AbsListView.LayoutParams(width, width));
            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }
        final PhotoItem gallery = (PhotoItem) getItem(position);

       
        //ImageLoaderUtils.displayLocalImage(gallery.getImageUri(), holder.sample,null);
       
       /***instead of using the ImageLoaderUtils provided which was loading images
        * not only slowly but also starting from the buttom then going up
        * I employ A custom Async dual Thread Class from polypicker, which was initially 
        * Copied from JB release framework:
        * https://android.googlesource.com/platform/frameworks/base/+/jb-release/core/java/android/os/AsyncTask.java
        *this loads images 2x faster than the stock supplied thread
        **/
        holder.imageFetcher.loadImage(Uri.parse(gallery.getImageUri()), holder.sample);
      
        return convertView;
    }

    class GalleryHolder {
        ImageView sample;
         ImageInternalFetcher imageFetcher = new ImageInternalFetcher(mContext, 300);
    }

}
