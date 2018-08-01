package com.bokecc.ccsskt.example.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class DragChildLayout extends RelativeLayout {

    private ViewDragHelper mDragHelper;
    private ArrayList<View> mViews;

    private View mDragView;

    // 记录最后的位置
    private float mLastX = -1;
    private float mLastY = -1;

    public DragChildLayout(Context context) {
        this(context, null);
    }

    public DragChildLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragChildLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewDragHelper();
    }

    private void restorePosition() {
        if (mLastX == -1 && mLastY == -1 && mDragView != null) { // 初始位置
            mLastX = getMeasuredWidth() - mDragView.getMeasuredWidth();
            mLastY = getMeasuredHeight() * 2 / 3;
        }
        if (mDragView != null) {
            mDragView.layout((int) mLastX, (int) mLastY,
                    (int) mLastX + mDragView.getMeasuredWidth(), (int) mLastY + mDragView.getMeasuredHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if ((mLastX == -1 && mLastY == -1) || changed) {
            super.onLayout(changed, l, t, r, b);
        }
//        restorePosition();
    }

    private void createViewDragHelper() {
        mViews = new ArrayList<>();
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                if (mViews.contains(child)) {
                    mDragView = child;
                    return true;
                }
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                //取得左边界的坐标
                final int leftBound = getPaddingLeft() + getPaddingStart();
                //取得右边界的坐标
                final int rightBound = getWidth() - getPaddingRight() - getPaddingEnd() - child.getWidth() - leftBound;
                //这个地方的含义就是 如果left的值 在leftbound和rightBound之间 那么就返回left
                //如果left的值 比 leftbound还要小 那么就说明 超过了左边界 那我们只能返回给他左边界的值
                //如果left的值 比rightbound还要大 那么就说明 超过了右边界，那我们只能返回给他右边界的值
                return Math.min(Math.max(left, leftBound), rightBound);
            }

            @Override
            public void onViewDragStateChanged(int state) {
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - getPaddingBottom() - child.getHeight() - topBound;
                return Math.min(Math.max(top, topBound), bottomBound);
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                mLastX = changedView.getX();
                mLastY = changedView.getY();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    public void setDragChildViews(View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        Collections.addAll(mViews, views);
    }

    public void reset() {
        mLastY = -1;
        mLastX = -1;
        requestLayout();
    }

}
