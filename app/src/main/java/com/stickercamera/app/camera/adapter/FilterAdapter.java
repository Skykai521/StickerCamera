package com.stickercamera.app.camera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.skykai.stickercamera.R;
import com.stickercamera.app.camera.effect.FilterEffect;
import com.stickercamera.app.camera.util.GPUImageFilterTools;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

/**
 * @author tongqian.ni
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.EffectHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<FilterEffect> filterUris;
    private final Context            mContext;
    private final Bitmap             background;

    private int                      selectFilter = 0;
    private OnItemClickListener      onItemClickListener;

    public FilterAdapter(Context context, List<FilterEffect> effects, Bitmap background) {
        this.mContext = context;
        this.filterUris = effects;
        this.background = background;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public void setSelectFilter(int selectFilter) {
        this.selectFilter = selectFilter;
    }

    public int getSelectFilter() {
        return selectFilter;
    }

    public FilterEffect getItem(int position) {
        return filterUris.get(position);
    }

    @NonNull
    @Override
    public EffectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_filter, parent, false);
        return new EffectHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EffectHolder holder, int position) {
        final FilterEffect effect = getItem(position);
        holder.filteredImg.setImage(background);
        holder.filterName.setText(effect.getTitle());
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(mContext, effect.getType());
        holder.filteredImg.setFilter(filter);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterUris.size();
    }

    static class EffectHolder extends RecyclerView.ViewHolder {
        GPUImageView filteredImg;
        TextView     filterName;

        EffectHolder(View itemView) {
            super(itemView);
            filteredImg = itemView.findViewById(R.id.small_filter);
            filterName = itemView.findViewById(R.id.filter_name);
        }
    }
}
