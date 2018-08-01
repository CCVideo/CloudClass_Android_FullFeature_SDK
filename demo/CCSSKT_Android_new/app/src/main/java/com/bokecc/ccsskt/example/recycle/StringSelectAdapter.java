package com.bokecc.ccsskt.example.recycle;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class StringSelectAdapter extends SelectAdapter<StringSelectAdapter.StringViewHolder, String> {

    public StringSelectAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.lianmai_item_layout;
    }

    @Override
    public StringViewHolder getViewHolder(View itemView, int viewType) {
        return new StringViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StringViewHolder holder, int position) {
        holder.mTip.setText(mDatas.get(position));
        if (position == mSelPosition) {
            holder.mIcon.setVisibility(View.VISIBLE);
            holder.mIcon.setImageResource(R.drawable.choose_icon);
        } else {
            holder.mIcon.setVisibility(View.GONE);
        }
    }

    static class StringViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_lianmai_type)
        TextView mTip;
        @BindView(R.id.id_choose_icon)
        ImageView mIcon;

        StringViewHolder(View itemView) {
            super(itemView);
        }
    }

}
