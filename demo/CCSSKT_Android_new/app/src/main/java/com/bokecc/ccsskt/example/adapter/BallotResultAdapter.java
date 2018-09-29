package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bokecc.sskt.bean.BallotResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 投票结果适配器
 * Created by wdh on 2018/6/14.
 */

public class BallotResultAdapter extends BaseRecycleAdapter<BallotResultAdapter
        .BallotItemViewHolder, BallotResult.choices> {

    public BallotResultAdapter(Context context) {
        super(context);
    }
    private static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }
    @Override
    public void onBindViewHolder(BallotItemViewHolder holder, int position) {
        BallotResult.choices choices = mDatas.get(position);
        int percent = 0;
        String str = "";
        if(Config.isBallot){
            str = Config.mBallotContent.get(position);
            percent = Integer.valueOf(choices.getContent());
            float  num = (float) percent/ Config.mBallotNum*100;
            holder.mOptionBar.setProgress((int)num);
        } else {
            str = Config.mBallotContent.get(position);
            percent = Integer.valueOf(choices.getContent());
            float  num = (float) percent/ Config.mBallotNum*100;
            holder.mOptionBar.setProgress((int)num);
        }
//        holder.mOptionSelectedNum.setText(": " + Config.mBallotContent.get(position));
        holder.mBallotResult.setText(percent + "票");
        switch (position) {
            case 0:
                holder.mOptionSelectedNum.setText(" A: " + str);
                break;
            case 1:
                holder.mOptionSelectedNum.setText(" B: " + str);
                break;
            case 2:
                holder.mOptionSelectedNum.setText(" C: " + str);
                break;
            case 3:
                holder.mOptionSelectedNum.setText(" D: " + str);
                break;
            case 4:
                holder.mOptionSelectedNum.setText(" E: " + str);
                break;
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.ballot_result_item;
    }

    @Override
    public BallotItemViewHolder getViewHolder(View itemView, int viewType) {
        return new BallotItemViewHolder(itemView);
    }

    final class BallotItemViewHolder extends BaseRecycleAdapter.BaseViewHolder {

//        @BindView(R.id.id_ballot_result_item_icon)
//        ImageView mOptionIcon;
        @BindView(R.id.id_ballot_result_item_num)
        TextView mOptionSelectedNum;
        @BindView(R.id.id_ballot_result_item_pb)
        ProgressBar mOptionBar;
        @BindView(R.id.id_ballot_result)
        TextView mBallotResult;

        BallotItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
