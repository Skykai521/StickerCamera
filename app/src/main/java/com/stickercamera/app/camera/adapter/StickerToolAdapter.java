package com.stickercamera.app.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.common.util.ImageLoaderUtils;
import com.github.skykai.stickercamera.R;
import com.stickercamera.app.model.Addon;

import java.util.List;

/**
 * 贴纸适配器
 *
 * @author tongqian.ni
 */
public class StickerToolAdapter extends RecyclerView.Adapter<StickerToolAdapter.EffectHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<Addon>   filterUris;
    private final Context       mContext;
    private OnItemClickListener onItemClickListener;

    public StickerToolAdapter(Context context, List<Addon> effects) {
        this.mContext = context;
        this.filterUris = effects;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public Addon getItem(int position) {
        return filterUris.get(position);
    }

    @NonNull
    @Override
    public EffectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_tool, parent, false);
        return new EffectHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EffectHolder holder, int position) {
        final Addon sticker = getItem(position);
        holder.container.setVisibility(View.GONE);
        ImageLoaderUtils.displayDrawableImage(sticker.getId() + "", holder.logo, null);
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
        ImageView logo;
        ImageView container;

        EffectHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.effect_image);
            container = itemView.findViewById(R.id.effect_background);
        }
    }
}
