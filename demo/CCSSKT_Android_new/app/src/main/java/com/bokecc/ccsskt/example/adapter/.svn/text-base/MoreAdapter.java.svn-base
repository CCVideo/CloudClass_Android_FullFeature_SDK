package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.MoreItem;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class MoreAdapter extends BaseRecycleAdapter<MoreAdapter.MoreViewHolder, MoreItem> {

    public MoreAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(MoreViewHolder holder, int position) {
        MoreItem moreItem = mDatas.get(position);
        holder.mIcon.setImageResource(moreItem.getResId());
        holder.mTip.setText(moreItem.getTip());
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.more_item_layout;
    }

    @Override
    public MoreViewHolder getViewHolder(View itemView, int viewType) {
        return new MoreViewHolder(itemView);
    }

    final class MoreViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_more_item_icon)
        ImageView mIcon;
        @BindView(R.id.id_more_item_tip)
        TextView mTip;

        MoreViewHolder(View itemView) {
            super(itemView);
        }
    }

}
