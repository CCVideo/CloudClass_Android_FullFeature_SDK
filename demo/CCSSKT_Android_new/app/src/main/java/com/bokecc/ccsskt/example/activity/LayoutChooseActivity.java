package com.bokecc.ccsskt.example.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.LayoutAdapter;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.sskt.CCInteractSession;

import java.util.ArrayList;

import butterknife.BindView;

import static com.bokecc.sskt.CCInteractSession.TEMPLATE_SINGLE;
import static com.bokecc.sskt.CCInteractSession.TEMPLATE_SPEAK;
import static com.bokecc.sskt.CCInteractSession.TEMPLATE_TILE;

public class LayoutChooseActivity extends TitleActivity<LayoutChooseActivity.LayoutViewHolder> {

    private static final String TAG = LayoutChooseActivity.class.getSimpleName();
    private static final int LAYOUT_COUNT = 3;

    private LayoutAdapter mLayoutAdapter;

    private SparseArrayCompat<Integer> mSelectedResids = new SparseArrayCompat<>();
    private SparseArrayCompat<Integer> mUnSelectedResids = new SparseArrayCompat<>();
    private SparseArrayCompat<Integer> mPositionTemplate = new SparseArrayCompat<>();
    private int mCurPosition;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_layout_choose;
    }

    @Override
    protected void beforeSetContentView() {
        if (CCApplication.sClassDirection == 1) {
            //取消标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected LayoutViewHolder getViewHolder(View contentView) {
        return new LayoutViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final LayoutViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("布局切换").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mSelectedResids.put(0, R.drawable.layout_lecture_selected);
        mSelectedResids.put(1, R.drawable.layout_main_video_selected);
        mSelectedResids.put(2, R.drawable.layout_tiling_selected);
        mUnSelectedResids.put(0, R.drawable.layout_lecture);
        mUnSelectedResids.put(1, R.drawable.layout_main_video);
        mUnSelectedResids.put(2, R.drawable.layout_tiling);
        mPositionTemplate.put(0, TEMPLATE_SPEAK);
        mPositionTemplate.put(1, TEMPLATE_SINGLE);
        mPositionTemplate.put(2, TEMPLATE_TILE);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        holder.mLayoutChooses.setLayoutManager(layoutManager);
        mLayoutAdapter = new LayoutAdapter(this);
        ArrayList<Integer> resids = new ArrayList<>();

        // 双师模式兼容为大屏模式
        int template = mInteractSession.getTemplate();
        if (template == CCInteractSession.TEMPLATE_DOUBLE_TEACHER) {
            template = CCInteractSession.TEMPLATE_SINGLE;
        }

        mCurPosition = mPositionTemplate.keyAt(mPositionTemplate.indexOfValue(template));
        for (int i = 0; i < LAYOUT_COUNT; i++) {
            if (mCurPosition == i) {
                resids.add(mSelectedResids.get(i));
            } else {
                resids.add(mUnSelectedResids.get(i));
            }
        }
        mLayoutAdapter.bindDatas(resids);
        holder.mLayoutChooses.setAdapter(mLayoutAdapter);

        holder.mLayoutChooses.addOnItemTouchListener(new BaseOnItemTouch(holder.mLayoutChooses, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                final int position = holder.mLayoutChooses.getChildAdapterPosition(viewHolder.itemView);
                if (mCurPosition == position) {
                    finish();
                    return;
                }
                showLoading();
                mInteractSession.changeRoomTemplateMode(mPositionTemplate.get(position), new CCInteractSession.AtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                        mLayoutAdapter.update(mCurPosition, mUnSelectedResids.get(mCurPosition));
                        mLayoutAdapter.update(position, mSelectedResids.get(position));
                        mCurPosition = position;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent data = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putInt("template", mPositionTemplate.get(mCurPosition));
                                data.putExtras(bundle);
                                setResult(Config.LAYOUT_RESULT_CODE, data);
                                finish();
                            }
                        }, 150);
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        toastOnUiThread(err);
                    }
                });

            }
        }));
    }

    final class LayoutViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_layout_chooses)
        RecyclerView mLayoutChooses;

         LayoutViewHolder(View view) {
            super(view);
        }

    }

}
