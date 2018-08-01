package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.NamedTime;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bokecc.ccsskt.example.util.TimeUtil;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class NamedTimeAdapter extends BaseRecycleAdapter<NamedTimeAdapter.NamedTimeViewHolder, NamedTime> {

    public NamedTimeAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(NamedTimeViewHolder holder, int position) {
        NamedTime namedTime = mDatas.get(position);
        holder.mTimeValue.setText(TimeUtil.format(namedTime.getSeconds()));
        if (namedTime.isSelected()) {
            holder.mSelectedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.mSelectedIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.named_time_item_layout;
    }

    @Override
    public NamedTimeViewHolder getViewHolder(View itemView, int viewType) {
        return new NamedTimeViewHolder(itemView);
    }

    final class NamedTimeViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_named_time_item_icon)
        ImageView mSelectedIcon;
        @BindView(R.id.id_named_time_item_value)
        TextView mTimeValue;

        NamedTimeViewHolder(View itemView) {
            super(itemView);
        }
    }

}
