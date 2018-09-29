package com.bokecc.ccsskt.example.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.util.TimeUtil;
import com.bokecc.ccsskt.example.view.ItemLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class NamedActivity extends TitleActivity<NamedActivity.NamedViewHolder> {

    private int mNamedContinueTime = 10;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_named;
    }

    @Override
    protected NamedViewHolder getViewHolder(View contentView) {
        return new NamedViewHolder(contentView);
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
    protected void onBindViewHolder(NamedViewHolder holder) {
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("点名").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        holder.mSetTime.setValue(TimeUtil.format(mNamedContinueTime));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    protected void onStop() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        mNamedContinueTime = data.getExtras().getInt("time");
        updateNamedContinueTime(mNamedContinueTime);
    }

    private void updateNamedContinueTime(int time) {
        mViewHolder.mSetTime.setValue(TimeUtil.format(time));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_ROLL_CALL_LIST:
                ArrayList<String> ids = (ArrayList<String>) event.obj2;
                if ((boolean) event.obj && ids != null && !ids.isEmpty()) {
                    CCApplication.mNamedCount = 0;
                    CCApplication.mNamedTotalCount = ids.size();
                    finish();
                    NamedCountActivity.startSelf(NamedActivity.this, mNamedContinueTime);
                    return;
                }
                if (!(boolean) event.obj) {
                    showToast("发送点名失败");
                    return;
                }
                if (ids == null || ids.isEmpty()) {
                    showToast("当前直播间没有学生");
                    return;
                }
                break;
        }
    }

    final class NamedViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_named_set_time)
        ItemLayout mSetTime;

        NamedViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.id_named_set_time)
        void setTime() {
            Bundle bundle = new Bundle();
            bundle.putInt("current_continue_time", mNamedContinueTime);
            go(NamedTimeActivity.class, Config.NAMED_REQUEST_CODE, bundle);
        }

        @OnClick(R.id.id_named_start)
        void startNamed() {
            mInteractSession.startNamed(mNamedContinueTime);
        }

    }

}
