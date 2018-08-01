package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.BrainStom;

/**
 * Created by wdh on 2018/6/4.
 */

public class BrainStomPopup extends BasePopupWindow {

    private OnCommitClickListener mOnCommitClickListener;
    private Button mCommit;
    private TextView mTip;
    private TextView mContent;
    private EditText mEditText;
    private LinearLayout mSelectZone;
    private BrainStom mBrainStom;
    public BrainStomPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {
        mSelectZone = findViewById(R.id.id_brain_select_zone);
        findViewById(R.id.id_brain_close).setOnClickListener(this);
        mTip = findViewById(R.id.id_brain_tip);
        mContent = findViewById(R.id.id_brain_content);
        mEditText = findViewById(R.id.text_info);
        mCommit = findViewById(R.id.id_brain_commit);
        mCommit.setOnClickListener(this);
        initCommit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.id_brain_close:
                dismiss();
                break;
            case R.id.id_brain_commit:
                dismiss();
                if (mOnCommitClickListener != null) {
                    mOnCommitClickListener.onCommit();
                }
                break;
        }
    }
    @Override
    protected int getContentView() {
        return R.layout.brain_stom_popup_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }
    public void show(BrainStom brainStom, View view) {
        mBrainStom = brainStom;
        mTip.setText(brainStom.getTileName());
        mContent.setText(brainStom.getContent());
        if(mEditText.getText()!=null){
            mEditText.setText("");
        }
        mCommit.setEnabled(false);
        super.show(view);
    }
    private void initCommit(){
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    mCommit.setEnabled(true);
                } else {
                    mCommit.setEnabled(false);
                }
            }
        });
    }
    public void sendBrainStomData(CCInteractSession interactSession) {
//        Config.isCommit = true;
        interactSession.sendBrainStomData(mBrainStom.getBrainStomID(), mEditText.getText()
                .toString().trim(),mBrainStom.getTileName());
    }
    public void dismiss(String voteid) {
        // FIXME: 2017/8/15 是否进行判断
        super.dismiss();
    }

    public void setOnCommitClickListener(OnCommitClickListener onCommitClickListener) {
        mOnCommitClickListener = onCommitClickListener;
    }

    public interface OnCommitClickListener {
        void onCommit();
    }
}
