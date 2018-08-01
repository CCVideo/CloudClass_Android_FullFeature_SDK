package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bokecc.sskt.bean.VoteResult;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class VoteResultAdapter extends BaseRecycleAdapter<VoteResultAdapter.VoteItemViewHolder, VoteResult.Statisic> {

    public VoteResultAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(VoteItemViewHolder holder, int position) {
        VoteResult.Statisic statisic = mDatas.get(position);
        int percent = Integer.valueOf(statisic.getPercent());
        holder.mOptionBar.setProgress(percent);
        holder.mOptionSelectedNum.setText(statisic.getCount() + "人(" + statisic.getPercent() + "%)");
        switch (statisic.getOption()) {
            case 0:
                if (mDatas.size() == 2) {
                    holder.mOptionIcon.setImageResource(R.drawable.r_tip);
                } else {
                    holder.mOptionIcon.setImageResource(R.drawable.a_tip);
                }
                break;
            case 1:
                if (mDatas.size() == 2) {
                    holder.mOptionIcon.setImageResource(R.drawable.w_tip);
                } else {
                    holder.mOptionIcon.setImageResource(R.drawable.b_tip);
                }
                break;
            case 2:
                holder.mOptionIcon.setImageResource(R.drawable.c_tip);
                break;
            case 3:
                holder.mOptionIcon.setImageResource(R.drawable.d_tip);
                break;
            case 4:
                holder.mOptionIcon.setImageResource(R.drawable.e_tip);
                break;
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.vote_result_item;
    }

    @Override
    public VoteItemViewHolder getViewHolder(View itemView, int viewType) {
        return new VoteItemViewHolder(itemView);
    }

    final class VoteItemViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_vote_result_item_icon)
        ImageView mOptionIcon;
        @BindView(R.id.id_vote_result_item_num)
        TextView mOptionSelectedNum;
        @BindView(R.id.id_vote_result_item_pb)
        ProgressBar mOptionBar;

        VoteItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
