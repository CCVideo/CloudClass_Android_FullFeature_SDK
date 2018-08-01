package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.IconEntity;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bumptech.glide.Glide;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class IconAdapter extends BaseRecycleAdapter<IconAdapter.IconViewHolder, IconEntity> {

    public IconAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(IconViewHolder holder, int position) {
        IconEntity iconEntity = getDatas().get(position);
        holder.mIconDes.setText(iconEntity.getResDes());
        Glide.with(mContext).load(iconEntity.getResId()).asBitmap().into(holder.mSrcIcon);
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.icon_item_layout;
    }

    @Override
    public IconViewHolder getViewHolder(View itemView, int viewType) {
        return new IconViewHolder(itemView);
    }

    final class IconViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_icon_src)
        ImageView mSrcIcon;
        @BindView(R.id.id_icon_des)
        TextView mIconDes;

        IconViewHolder(View itemView) {
            super(itemView);
        }
    }

}
