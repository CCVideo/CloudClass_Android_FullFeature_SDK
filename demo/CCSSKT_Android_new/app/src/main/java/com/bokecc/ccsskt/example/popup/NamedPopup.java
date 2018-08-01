package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;
import com.bokecc.ccsskt.example.util.TimeUtil;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class NamedPopup extends BasePopupWindow {

    private static final String TIP = "点名倒计时：";
    private TextView mNamedTimeTip;
    private Handler mHandler;
    private int mTime = -1;
    private boolean flag = false;

    private OnAnswerClickListener mOnAnswerClickListener;

    private Runnable mCountTask = new Runnable() {
        @Override
        public void run() {
            if (!flag) {
                return;
            }
            mTime -= 1;
            if (mTime <= 0) {
                dismiss();
                stopCountDown();
                return;
            }
            updateTimeTip();
            mHandler.postDelayed(this, 1000);
        }
    };

    public NamedPopup(Context context) {
        super(context);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public NamedPopup(Context context, int width, int height) {
        super(context, width, height);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onViewCreated() {
        mNamedTimeTip = findViewById(R.id.id_named_time_tip);
        findViewById(R.id.id_named_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.id_named_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnAnswerClickListener != null) {
                    mOnAnswerClickListener.onAnswer();
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.named_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefTranslateEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefTranslateExitAnim();
    }

    public void show(int time, View view) {
        super.show(view);
        mTime = time;
        updateTimeTip();
        startCountDown();
    }

    @Override
    public void dismiss() {
        stopCountDown();
        super.dismiss();
    }

    public void setOnAnswerClickListener(OnAnswerClickListener onAnswerClickListener) {
        mOnAnswerClickListener = onAnswerClickListener;
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        flag = true;
        mHandler.postDelayed(mCountTask, 1000);
    }

    /**
     * 结束倒计时
     */
    private void stopCountDown() {
        flag = false;
        mHandler.removeCallbacks(mCountTask);
    }

    private void updateTimeTip() {
        String timeStr = TimeUtil.formatNamed(mTime);
        timeStr = TIP + timeStr;
        SpannableString spannableString = new SpannableString(timeStr);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary));
        spannableString.setSpan(colorSpan, TIP.length(), timeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mNamedTimeTip.setText(spannableString);
    }

    public interface OnAnswerClickListener {
        void onAnswer();
    }

}
