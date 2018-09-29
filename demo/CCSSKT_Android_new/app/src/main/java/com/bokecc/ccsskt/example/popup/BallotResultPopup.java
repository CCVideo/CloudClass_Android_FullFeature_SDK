package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.BallotResultAdapter;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.sskt.bean.Ballot;
import com.bokecc.sskt.bean.BallotResult;

import java.util.ArrayList;

/**
 * 投票统计实例类
 * Created by wdh on 2018/6/12.
 */

public class BallotResultPopup extends BasePopupWindow {

    private final int rightZimuIcons[] = new int[]{
            R.drawable.a_right_icon,
            R.drawable.b_right_icon,
            R.drawable.c_right_icon,
            R.drawable.d_right_icon,
            R.drawable.e_right_icon
    };
    private BallotResultAdapter mBallotResultAdapter;

    private TextView mBallotTip;
    private TextView mBallotContent;
    private TextView mText;
    private ImageView  mRightImgs[];
    public BallotResultPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {
        mBallotTip = findViewById(R.id.id_ballot_result_tip);
        mBallotContent = findViewById(R.id.id_ballot_result_statistics);
        mText = findViewById(R.id.id_vote_result_right);
        RecyclerView resultContent = findViewById(R.id.id_ballot_result_content);
        ImageView rightImg1 = findViewById(R.id.id_ballot_result_right_img1);
        ImageView rightImg2 = findViewById(R.id.id_ballot_result_right_img2);
        ImageView rightImg3 = findViewById(R.id.id_ballot_result_right_img3);
        ImageView rightImg4 = findViewById(R.id.id_ballot_result_right_img4);
        ImageView rightImg5 = findViewById(R.id.id_ballot_result_right_img5);

        resultContent.setLayoutManager(new LinearLayoutManager(mContext));
        mBallotResultAdapter = new BallotResultAdapter(mContext);
        resultContent.setAdapter(mBallotResultAdapter);

        mRightImgs = new ImageView[]{
                rightImg1, rightImg2, rightImg3, rightImg4, rightImg5
        };

        findViewById(R.id.id_ballot_reslut_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public void show(BallotResult ballotResult, ArrayList<Integer> results, View view) {
        mBallotTip.setText("主题：" + Config.mBallotTitle);
        Config.mBallotNum = ballotResult.getBallotResultNum();
        mBallotContent.setText("已有" + ballotResult.getBallotResultNum() + "人投票");
        if(ballotResult.getBallotResultNum() != 0){
            if(Config.mSeleteType == Ballot.SINGLE) {
                for(int i = 0; i< Config.mBallotResults.size(); i++){
                    mRightImgs[0].setVisibility(View.VISIBLE);
                    mRightImgs[0].setImageResource(rightZimuIcons[Config.mBallotResults.get(i)]);
                }
            } else {
                for (int i = 0; i < Config.mBallotResults.size(); i++) { // 多选
                    mRightImgs[i].setVisibility(View.VISIBLE);
                    mRightImgs[i].setImageResource(rightZimuIcons[Config.mBallotResults.get(i)]);
                }
            }
            mBallotResultAdapter.setDatas(ballotResult.getContent());
            mBallotResultAdapter.notifyDataSetChanged();
        } else {
            BallotResult mBallotResult = new BallotResult();
//            ArrayList<BallotResult.choices> choices = new ArrayList<>();
//            for (int i = 0; i < ballotResult.getContent().size(); i++) {
//                BallotResult.choices mChoices = new BallotResult.choices();
//                mChoices.setContent(Config.mBallotContent.get(i));
//                choices.add(mChoices);
//            }
//            mBallotResult.setContent(choices);
            for (int i = 0; i < mRightImgs.length; i++) {
                mRightImgs[i].setVisibility(View.GONE);
            }
            mBallotResultAdapter.setDatas(ballotResult.getContent());
            mBallotResultAdapter.notifyDataSetChanged();
        }
        super.show(view);
    }
    @Override
    protected int getContentView() {
        return R.layout.ballot_result_popup_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }
}
