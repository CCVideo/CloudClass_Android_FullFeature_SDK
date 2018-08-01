package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class CommonPopup extends BasePopupWindow {

    private TextView mTip;
    private Button mOk, mCancel;

    private OnOKClickListener mOnOKClickListener;
    private OnCancelClickListener mOnCancelClickListener;

    private boolean isOk = false;

    public CommonPopup(Context context) {
        super(context);
    }

    public CommonPopup(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    protected void onViewCreated() {
        mTip = findViewById(R.id.id_choose_dialog_tip);
        mOk = findViewById(R.id.id_choose_dialog_ok);
        mCancel = findViewById(R.id.id_choose_dialog_cancel);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOk = true;
                dismiss();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOk = false;
                dismiss();
            }
        });

        setOnPopupDismissListener(new OnPopupDismissListener() {
            @Override
            public void onDismiss() {
                if (isOk) {
                    if (mOnOKClickListener != null) {
                        mOnOKClickListener.onClick();
                    }
                } else {
                    if (mOnCancelClickListener != null) {
                        mOnCancelClickListener.onClick();
                    }
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.common_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }

    public void setTip(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        mTip.setText(value);
    }

    public void setOKValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        mOk.setText(value);
    }

    public void setCancelValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        mCancel.setText(value);
    }

    public void setOKClickListener(OnOKClickListener okClickListener) {
        mOnOKClickListener = okClickListener;
    }

    public void setCancelClickListener(OnCancelClickListener cancelClickListener) {
        mOnCancelClickListener = cancelClickListener;
    }

    public interface OnOKClickListener {
        void onClick();
    }

    public interface OnCancelClickListener {
        void onClick();
    }

}
