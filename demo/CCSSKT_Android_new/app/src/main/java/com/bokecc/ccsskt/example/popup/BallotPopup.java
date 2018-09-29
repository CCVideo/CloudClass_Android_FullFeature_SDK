package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.BallotAdapter;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.util.SpacesItemDecoration;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.Ballot;

/**
 * 投票实例
 * Created by wdh on 2018/6/6.
 */

public class BallotPopup extends BasePopupWindow {

    private TextView mBallotTile;
    private OnCommitClickListener mOnCommitClickListener;
    private Button mCommit;
    public BallotPopup(Context context) {
        super(context);
    }
    private BallotAdapter mBallotAdapter;
    private Ballot mBallot;
    private String mBallotContent;
    private int prePos = -1;
    private boolean flag = false;
    @Override
    protected void onViewCreated() {
        mBallotTile = findViewById(R.id.id_ballot_tip);
        final RecyclerView resultContent = findViewById(R.id.id_ballot_content);
        mCommit = findViewById(R.id.id_ballot_commit);
        resultContent.setLayoutManager(new LinearLayoutManager(mContext));
        resultContent.addItemDecoration(new SpacesItemDecoration(15));
        mBallotAdapter = new BallotAdapter(mContext);
        resultContent.setAdapter(mBallotAdapter);
        mBallotAdapter.setSelPosition(-1);
        findViewById(R.id.id_ballot_close).setOnClickListener(this);
        mCommit.setOnClickListener(this);
        resultContent.addOnItemTouchListener(new BaseOnItemTouch(resultContent, new OnClickListener() {

            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                int position = resultContent.getChildAdapterPosition(viewHolder.itemView);
                if(mBallot.getBallotType() == Ballot.SINGLE){
                    Config.mBallotResults.clear();
                    Config.mBallotResults.add(position);
                } else {
                    if(position != prePos){
                        if(Config.mBallotResults.size() >= 0){
                            for(int i = 0; i< Config.mBallotResults.size(); i++){
                                Log.i("1", "onClick: "+ Config.mBallotResults.get(i));
                                if(Config.mBallotResults.get(i) == position){
                                    Config.mBallotResults.remove(i);
                                    flag = true;
                                }
                            }
                            if(Config.mBallotResults.size() <= mBallot.getContent().size()){
                                if(!flag){
                                    Config.mBallotResults.add(position);
                                    prePos = position;
                                }
                                flag = false;
                            } else {
                                for(int i = 0; i< Config.mBallotResults.size(); i++){
                                    Log.i("1", "onClick: "+ Config.mBallotResults.get(i));
                                    if(Config.mBallotResults.get(i) == position){
                                        Config.mBallotResults.remove(i);
                                    }
                                }
                                prePos = -1;
                            }
                        }
                    } else {
                        if(Config.mBallotResults.size() > 0){
                            for(int i = 0; i< Config.mBallotResults.size(); i++){
                                Log.i("2", "onClick: "+ Config.mBallotResults.get(i));
                                if(Config.mBallotResults.get(i) == position){
                                    Config.mBallotResults.remove(i);
                                }
                            }
                        }
                        prePos = -1;
                    }
                }
                mBallotAdapter.setSelPosition(position);
                if(Config.mBallotResults.isEmpty()){
                    mCommit.setEnabled(false);
                } else {
                    mCommit.setEnabled(true);
                }
            }
        }));
        mBallotAdapter.setOnSelectOnClickListener(new BallotAdapter.OnSelectOnClickListener() {

            @Override
            public void onSelect(int position, Ballot.Statisic statisic) {
                Log.i("wdh", "onSelect: "+ position + statisic.getContent());
                mBallotContent = statisic.getContent();
                Config.mBallotResults.add(position);
                mCommit.setEnabled(true);
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.id_ballot_close:
                dismiss();
                break;
            case R.id.id_ballot_commit:
                dismiss();
                if (mOnCommitClickListener != null) {
                    mOnCommitClickListener.onCommit();
                    Config.isBallot = true;
                }
                break;
        }
    }
    public void show(Ballot ballot, View view) {
        mBallot = ballot;
        Config.mSeleteType = mBallot.getBallotType();
        Config.mBallotTitle = ballot.getTileName();
        mBallotTile.setText("主题：" + ballot.getTileName());
        mCommit.setEnabled(false);
        Config.mBallotResults.clear();
        Config.mBallotContent.clear();
        Config.isBallot = false;
        Config.mBallotNum = 0;
        prePos = -1;
        mBallotAdapter.setSelPosition(-1);
        Config.mBallot.add(ballot.getContent());
        mBallotAdapter.setDatas(ballot.getContent());
        mBallotAdapter.notifyDataSetChanged();
        super.show(view);
    }

    public void dismiss(String ballotid) {
        // FIXME: 2017/8/15 是否进行判断
        super.dismiss();
    }

    public void sendBallotData(CCInteractSession interactSession) {
        interactSession.sendBallotData(mBallot.getBallotId(), Config.mBallotResults,mBallot.getTileName());
    }

    public void setOnCommitClickListener(OnCommitClickListener onCommitClickListener) {
        mOnCommitClickListener = onCommitClickListener;
    }

    public interface OnCommitClickListener {
        void onCommit();
    }

    @Override
    protected int getContentView() {
        return R.layout.ballot_popup_layout;
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
