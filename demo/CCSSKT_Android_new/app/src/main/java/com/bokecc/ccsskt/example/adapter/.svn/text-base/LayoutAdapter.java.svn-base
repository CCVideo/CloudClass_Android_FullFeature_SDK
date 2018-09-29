package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bumptech.glide.Glide;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class LayoutAdapter extends BaseRecycleAdapter<LayoutAdapter.LayoutViewHolder, Integer> {

    public LayoutAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(LayoutViewHolder holder, int position) {
        Glide.with(mContext).load(mDatas.get(position)).asBitmap().into(holder.mLayoutIcon);
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.layout_img_layout;
    }

    @Override
    public LayoutViewHolder getViewHolder(View itemView, int viewType) {
        return new LayoutViewHolder(itemView);
    }

    final class LayoutViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_layout_img)
        ImageView mLayoutIcon;

        LayoutViewHolder(View itemView) {
            super(itemView);
        }
    }

}
