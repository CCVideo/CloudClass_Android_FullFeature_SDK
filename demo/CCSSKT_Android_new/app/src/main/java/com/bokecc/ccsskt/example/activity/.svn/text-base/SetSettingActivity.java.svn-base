package com.bokecc.ccsskt.example.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.recycle.RecycleViewDivider;
import com.bokecc.ccsskt.example.recycle.StringSelectAdapter;
import com.bokecc.sskt.CCInteractSession;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;

;

public class SetSettingActivity extends TitleActivity<SetSettingActivity.LianMaiViewHolder> {

    @BindString(R.string.setting_media_both)
    String mMediaTypeBoth;
    @BindString(R.string.setting_media_audio)
    String mMediaTypeAudio;
    @BindString(R.string.setting_lianmai_free)
    String mLianmaiTypeFree;
    @BindString(R.string.setting_lianmai_named)
    String mLianmaiTypeNamed;
    @BindString(R.string.setting_lianmai_auto)
    String mLianmaiTypeAuto;

    public static final int SETTING_MODE_MEDIA = 0;
    public static final int SETTING_MODE_LIANMAI = 1;

    private int mSettingMode = 0;

    private static final String KEY_SELECT_POSITION = "select_position";
    private static final String KEY_SETTING_MODE = "setting_mode";

    private static Intent newIntent(Context context, int position, int mode) {
        Intent intent = new Intent(context, SetSettingActivity.class);
        intent.putExtra(KEY_SELECT_POSITION, position);
        intent.putExtra(KEY_SETTING_MODE, mode);
        return intent;
    }

    public static void startSelf(Context context, int position, int mode) {
        context.startActivity(newIntent(context, position, mode));
    }

    View mRoot;
    private StringSelectAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lian_mai_setting;
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
    protected LianMaiViewHolder getViewHolder(View contentView) {
        return new LianMaiViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final LianMaiViewHolder holder) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).
                title("连麦模式").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        returnBack();
                    }
                }).
                build();
        setTitleOptions(options);

        final int selPosition = getIntent().getExtras().getInt(KEY_SELECT_POSITION);
        mSettingMode = getIntent().getExtras().getInt(KEY_SETTING_MODE);
        ArrayList<String> mDatas = new ArrayList<>();
        if (mSettingMode == SETTING_MODE_LIANMAI) {
            mDatas.add(mLianmaiTypeFree);
            mDatas.add(mLianmaiTypeNamed);
            mDatas.add(mLianmaiTypeAuto);
        } else {
            mDatas.add(mMediaTypeAudio);
            mDatas.add(mMediaTypeBoth);
        }
        holder.mLianmaiType.setLayoutManager(new LinearLayoutManager(this));
        holder.mLianmaiType.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, 1, Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BETWEEN));
        mAdapter = new StringSelectAdapter(this);
        mAdapter.bindDatas(mDatas);
        mAdapter.setSelPosition(selPosition); // 设置默认选中
        holder.mLianmaiType.addOnItemTouchListener(new BaseOnItemTouch(holder.mLianmaiType,
                new OnClickListener() {
                    @Override
                    public void onClick(RecyclerView.ViewHolder viewHolder) {
                        final int curPosition = holder.mLianmaiType.getChildAdapterPosition(viewHolder.itemView);
                        if (selPosition == curPosition) {
                            finish();
                            return;
                        }
                        showLoading();
                        if (mSettingMode == SETTING_MODE_MEDIA) {
                            mInteractSession.setMediaMode(curPosition == 0 ? CCInteractSession.MEDIA_MODE_AUDIO : CCInteractSession.MEDIA_MODE_BOTH,
                                    new CCInteractSession.AtlasCallBack<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAdapter.setSelPosition(curPosition);
                                            dismissLoading();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 300L);
                                        }

                                        @Override
                                        public void onFailure(String err) {
                                            dismissLoading();
                                            toastOnUiThread(err);
                                        }
                                    });
                        } else {
                            mInteractSession.setLianmaiMode(curPosition == 0 ? CCInteractSession.LIANMAI_MODE_FREE :
                                            curPosition == 1 ? CCInteractSession.LIANMAI_MODE_NAMED : CCInteractSession.LIANMAI_MODE_AUTO,
                                    new CCInteractSession.AtlasCallBack<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAdapter.setSelPosition(curPosition);
                                            dismissLoading();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 300L);
                                        }

                                        @Override
                                        public void onFailure(String err) {
                                            dismissLoading();
                                            toastOnUiThread(err);
                                        }
                                    });
                        }
                    }
                }));
        holder.mLianmaiType.setAdapter(mAdapter);

    }

    private void returnBack() {
        setResult(-1);
        exit();
    }

    private void exit() {
        setResult(Config.SELECT_RESULT_CODE);
        finish();
    }

    final class LianMaiViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_lianmai_datas)
        RecyclerView mLianmaiType;

        LianMaiViewHolder(View view) {
            super(view);
        }
    }

}
