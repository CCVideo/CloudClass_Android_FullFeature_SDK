package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.ColorStatus;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class ColorAdapter extends BaseRecycleAdapter<ColorAdapter.ColorViewHolder, ColorStatus> {

    public ColorAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        holder.mColor.setSelected(getDatas().get(position).isSelected());
        holder.mColor.setBackgroundResource(getDatas().get(position).getResId());
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.draw_bubble_color_item;
    }

    @Override
    public ColorViewHolder getViewHolder(View itemView, int viewType) {
        return new ColorViewHolder(itemView);
    }

    final class ColorViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_bubble_color)
        ImageButton mColor;

        ColorViewHolder(View itemView) {
            super(itemView);
        }
    }

}
