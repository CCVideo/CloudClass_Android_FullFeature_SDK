package com.bokecc.ccsskt.example.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import com.bokecc.ccsskt.example.util.DensityUtil;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

@SuppressLint("AppCompatCustomView")
public class MaxLimitEditText extends EditText {

    private static final String TAG = MaxLimitEditText.class.getSimpleName();

    private Context mContext;

    private Paint mPaint, mNumPaint;
    private int mMaxLimit = 30, mLastNum = mMaxLimit;

    public MaxLimitEditText(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MaxLimitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MaxLimitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        setGravity(Gravity.TOP|Gravity.LEFT);
        mPaint = getPaint();
        mNumPaint = new Paint();
        mNumPaint.setAntiAlias(true);
        mNumPaint.setColor(Color.parseColor("#666666"));
        mNumPaint.setTextSize(DensityUtil.sp2px(mContext, 12));
        setPadding(DensityUtil.dp2px(mContext, 10), DensityUtil.dp2px(mContext, 5), (int) (calNumWidth() + DensityUtil.dp2px(mContext, 3)), DensityUtil.dp2px(mContext, 5));
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 30) {
                    s.delete(30, s.length());
                }
                mLastNum = mMaxLimit - s.length();
            }
        });
    }

    private float calNumWidth() {
        return mNumPaint.measureText(String.valueOf(mMaxLimit));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLastNum(canvas);
    }

    private void drawLastNum(Canvas canvas) {
        float x = getWidth() - calNumWidth() - DensityUtil.dp2px(mContext, 7);
        float y = getHeight() - DensityUtil.dp2px(mContext, 7);
        canvas.drawText(String.valueOf(mLastNum), x, y, mNumPaint);
    }

}
