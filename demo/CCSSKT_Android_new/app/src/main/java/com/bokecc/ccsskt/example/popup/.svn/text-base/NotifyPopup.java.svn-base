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

public class NotifyPopup extends BasePopupWindow {

    private TextView mTip;
    private Button mOk;

    private OnOKClickListener mOnOKClickListener;

    public NotifyPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {
        mTip = findViewById(R.id.id_notify_tip);
        mOk = findViewById(R.id.id_notify_ok);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setOnPopupDismissListener(new OnPopupDismissListener() {
            @Override
            public void onDismiss() {
                if (mOnOKClickListener != null) {
                    mOnOKClickListener.onClick();
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.notify_layout;
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

    public void setOKClickListener(OnOKClickListener okClickListener) {
        mOnOKClickListener = okClickListener;
    }

    public interface OnOKClickListener {
        void onClick();
    }
}
