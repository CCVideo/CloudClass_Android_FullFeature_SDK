package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.Vote;

import java.util.ArrayList;

import static com.bokecc.ccsskt.example.global.Config.mResults;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class VotePopup extends BasePopupWindow {

    private final int optionUnselectedids[] = new int[]{
            R.drawable.a_unselected, R.drawable.b_unselected, R.drawable.c_unselected,
            R.drawable.d_unselected, R.drawable.e_unselected
    };
    private final int optionSelectedids[] = new int[]{
            R.drawable.a_selected, R.drawable.b_selected, R.drawable.c_selected,
            R.drawable.d_selected, R.drawable.e_selected
    };
    private final int judgeUnselectedids[] = new int[]{
            R.drawable.r_unselected, R.drawable.w_unselected
    };
    private final int judgeSelectedids[] = new int[]{
            R.drawable.r_selected, R.drawable.w_selected
    };

    private LinearLayout mSelectZone;
    private Button mCommit;
    private ImageButton mOptionViews[];
    private int[] optionUnselected;
    private int[] optionSelected;

    private boolean isSingle = true; // 是否是单选题默认是

    private Vote mVote;

    private OnCommitClickListener mOnCommitClickListener;

    public VotePopup(Context context) {
        super(context);
    }

    public VotePopup(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    protected void onViewCreated() {
        mSelectZone = findViewById(R.id.id_vote_select_zone);
        findViewById(R.id.id_vote_close).setOnClickListener(this);
        mCommit = findViewById(R.id.id_vote_commit);
        mCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.id_vote_close:
                dismiss();
                break;
            case R.id.id_vote_commit:
                dismiss();
                if (mOnCommitClickListener != null) {
                    mOnCommitClickListener.onCommit();
                }
                break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.vote_popup_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }

    private void setAnswerCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        boolean isJudgment = count == 2;
        mSelectZone.removeAllViews();
        mOptionViews = new ImageButton[count];
        for (int i = 0; i < count; i++) {
            addChild4SelectZone(i, isJudgment);
        }
    }

    private void addChild4SelectZone(int index, boolean isJudgment) {
        LinearLayout itemRoot = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        itemRoot.setOrientation(LinearLayout.VERTICAL);
        itemRoot.setLayoutParams(params);
        mSelectZone.addView(itemRoot);
        int resId;
        if (isJudgment) {
            optionSelected = judgeSelectedids;
            optionUnselected = judgeUnselectedids;
            resId = judgeUnselectedids[index];
        } else {
            optionSelected = optionSelectedids;
            optionUnselected = optionUnselectedids;
            resId = optionUnselectedids[index];
        }
        ImageButton item = new ImageButton(mContext);
        params = new LinearLayout.LayoutParams(DensityUtil.dp2px(mContext, 50),
                DensityUtil.dp2px(mContext, 50));
        params.gravity = Gravity.CENTER;
        item.setLayoutParams(params);
        item.setBackgroundResource(resId);
        item.setTag(index);
        item.setOnClickListener(new MyOptionClickListener());
        itemRoot.addView(item);
        mOptionViews[index] = item;
    }

    private void resetOption() {
        mResults.clear();
        for (int i = 0; i < mOptionViews.length; i++) {
            mOptionViews[i].setBackgroundResource(optionUnselected[i]);
        }
    }

    private class MyOptionClickListener implements View.OnClickListener {

        private boolean isSelected = false; // 是否被选中默认为选中

        MyOptionClickListener() {
        }

        @Override
        public void onClick(View v) {
            if (isSingle && !isSelected) { // 单选 并且上一次的选择不是该选项
                resetOption();
            }
            if (isSelected) {
                mResults.remove(Integer.valueOf(String.valueOf(v.getTag())));
                v.setBackgroundResource(optionUnselected[(int) v.getTag()]);
            } else {
                mResults.add((Integer) v.getTag());
                v.setBackgroundResource(optionSelected[(int) v.getTag()]);
            }
            isSelected = !isSelected;
            if (mResults.isEmpty()) {
                mCommit.setEnabled(false);
            } else {
                mCommit.setEnabled(true);
            }
        }
    }

    public void show(Vote vote, View view) {
        mVote = vote;
        Config.isCommit = false;
        this.isSingle = vote.getVoteType() == Vote.SINGLE;
        setAnswerCount(vote.getVoteCount());
        mResults.clear();
        mCommit.setEnabled(false);
        super.show(view);
    }

    public void dismiss(String voteid) {
        // FIXME: 2017/8/15 是否进行判断
        super.dismiss();
    }

    public void sendVoteSelected(CCInteractSession interactSession) {
        Config.isCommit = true;
        interactSession.sendVoteSelected(mVote.getVoteId(), mVote.getPublisherId(), isSingle, mResults);
    }

    public ArrayList<Integer> getResults() {
        return Config.isCommit ? mResults : null;
    }

    public void setOnCommitClickListener(OnCommitClickListener onCommitClickListener) {
        mOnCommitClickListener = onCommitClickListener;
    }

    public interface OnCommitClickListener {
        void onCommit();
    }

}
