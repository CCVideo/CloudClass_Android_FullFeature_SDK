package com.bokecc.ccsskt.example.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.popup.BottomCancelPopup;
import com.bokecc.ccsskt.example.view.ItemLayout;
import com.bokecc.ccsskt.example.view.ToggleButton;
import com.bokecc.sskt.CCInteractSession;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

import static com.bokecc.sskt.CCInteractSession.LIANMAI_MODE_AUTO;
import static com.bokecc.sskt.CCInteractSession.LIANMAI_MODE_FREE;
import static com.bokecc.sskt.CCInteractSession.MEDIA_MODE_AUDIO;

public class SettingActivity extends TitleActivity<SettingActivity.SettingViewHolder> {

    private static final String TAG = SettingActivity.class.getSimpleName();

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

    @BindString(R.string.setting_bitrate)
    String mBitrateTip;

    @BindString(R.string.setting_bitrate_lowest)
    String lowest;
    @BindString(R.string.setting_bitrate_lower)
    String lower;
    @BindString(R.string.setting_bitrate_medium)
    String medium;
    @BindString(R.string.setting_bitrate_higher)
    String higher;
    @BindString(R.string.setting_bitrate_highest)
    String highest;
    @BindString(R.string.kick_down_mai_tip)
    String kickTip;

    private int mMediaMode;
    private int mLianmaiMode;

    private Map<Integer, String> mBitrateTips;
    private int mPickIndex = 0;

    private int mLoopTime = 10;

    private CCInteractSession.AtlasCallBack<Void> mGagCallBack = new CCInteractSession.AtlasCallBack<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            dismissLoading();
        }

        @Override
        public void onFailure(String err) {
            dismissLoading();
            toastOnUiThread(err);
            mViewHolder.mGag.setChecked(mInteractSession.isRoomGag());
        }
    };

    private CCInteractSession.AtlasCallBack<Void> mAudioCallBack = new CCInteractSession.AtlasCallBack<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            dismissLoading();
        }

        @Override
        public void onFailure(String err) {
            dismissLoading();
            toastOnUiThread(err);
            mViewHolder.mMic.setChecked(mInteractSession.isRoomGag());
        }
    };

//    private CCInteractSession.AtlasCallBack<Void> mLoopCallBack = new CCInteractSession.AtlasCallBack<Void>() {
//        @Override
//        public void onSuccess(Void aVoid) {
//            dismissLoading();
//        }
//
//        @Override
//        public void onFailure(String err) {
//            dismissLoading();
//            toastOnUiThread(err);
//            mViewHolder.mLoop.setChecked(true);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected SettingViewHolder getViewHolder(View contentView) {
        return new SettingViewHolder(contentView);
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
    protected void onBindViewHolder(final SettingViewHolder holder) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("设置").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mBitrateTips = new HashMap<>();
        mBitrateTips.put(CCInteractSession.Bitrate_Level1, lowest);
        mBitrateTips.put(CCInteractSession.Bitrate_Level2, lower);
        mBitrateTips.put(CCInteractSession.Bitrate_Level3, medium);
        mBitrateTips.put(CCInteractSession.Bitrate_Level4, higher);
        mBitrateTips.put(CCInteractSession.Bitrate_Level5, highest);

        mMediaMode = mInteractSession.getMediaMode();
        mLianmaiMode = mInteractSession.getLianmaiMode();

        holder.mMediaModeSetting.setValue(mMediaMode == MEDIA_MODE_AUDIO ? mMediaTypeAudio :
                mMediaTypeBoth);
        holder.mLianmaiSetting.setValue(mLianmaiMode == LIANMAI_MODE_FREE ? mLianmaiTypeFree :
                mLianmaiMode == LIANMAI_MODE_AUTO ? mLianmaiTypeAuto : mLianmaiTypeNamed);

        holder.mGag.setChecked(mInteractSession.isRoomGag());
        holder.mGag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                showLoading();
                if (isChecked) {
                    mInteractSession.gagAll(mGagCallBack);
                } else {
                    mInteractSession.cancelGagAll(mGagCallBack);
                }
            }
        });

        holder.mMic.setChecked(!mInteractSession.isAllAllowAudio());
        holder.mMic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                showLoading();
                mInteractSession.changeRoomAudioState(!isChecked, mAudioCallBack);
            }
        });

//        if (mInteractSession.getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
//            holder.mLoopPlayItem.setVisibility(View.VISIBLE);
//            holder.mLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    mInteractSession.changeRoomRotate(isChecked ? CCInteractSession.ROTATE_STATUS_OPEN : CCInteractSession.ROTATE_STATUS_CLOSE, mLoopTime, mLoopCallBack);
//                }
//            });
//        } else {
//            holder.mLoopPlayItem.setVisibility(View.GONE);
//            holder.mLoopPlayTime.setVisibility(View.GONE);
//        }

        holder.mSBitrate.setValue(mBitrateTips.get(mInteractSession.getTalkerBitrate()));
        holder.mTBitrate.setValue(mBitrateTips.get(mInteractSession.getPresenterBitrate()));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mInteractSession.getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
//            mViewHolder.mLoopPlayItem.setVisibility(View.VISIBLE);
//            if (mInteractSession.getSpeakRotate().getStatus() == CCInteractSession.ROTATE_STATUS_CLOSE) {
//                mViewHolder.mLoop.setChecked(false);
//                mViewHolder.mLoopPlayTime.setVisibility(View.GONE);
//            } else {
//                mViewHolder.mLoop.setChecked(true);
//                mViewHolder.mLoopPlayTime.setVisibility(View.VISIBLE);
//            }
//        } else {
//            mViewHolder.mLoopPlayItem.setVisibility(View.GONE);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_UPDATE_MEDIA_MODE:
                mMediaMode = (int) event.obj;
                mViewHolder.mMediaModeSetting.setValue(mMediaMode == MEDIA_MODE_AUDIO ? mMediaTypeAudio :
                        mMediaTypeBoth);
                break;
            case Config.INTERACT_EVENT_WHAT_UPDATE_LIANMAI_MODE:
                mLianmaiMode = (int) event.obj;
                mViewHolder.mLianmaiSetting.setValue(mLianmaiMode == LIANMAI_MODE_FREE ? mLianmaiTypeFree :
                        mLianmaiMode == LIANMAI_MODE_AUTO ? mLianmaiTypeAuto : mLianmaiTypeNamed);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(int time) {
        mLoopTime = time;
    }

    final class SettingViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_item_gag)
        ToggleButton mGag;
        @BindView(R.id.id_item_close_mic)
        ToggleButton mMic;
        @BindView(R.id.id_setting_media_mode)
        ItemLayout mMediaModeSetting;
        @BindView(R.id.id_setting_lianmai_mode)
        ItemLayout mLianmaiSetting;
        @BindView(R.id.id_setting_loop_play_time)
        ItemLayout mLoopPlayTime;
        @BindView(R.id.id_setting_sbitrate)
        ItemLayout mSBitrate;
        @BindView(R.id.id_setting_tbitrate)
        ItemLayout mTBitrate;
//        @BindView(R.id.id_setting_loop_play)
//        RelativeLayout mLoopPlayItem;
//        @BindView(R.id.id_item_loop_play)
//        ToggleButton mLoop;

        private final ArrayList<String> bitOptions = new ArrayList<>();
        private final int[] bitValues = new int[]{CCInteractSession.Bitrate_Level1, CCInteractSession.Bitrate_Level2,
                CCInteractSession.Bitrate_Level3, CCInteractSession.Bitrate_Level4, CCInteractSession.Bitrate_Level5};
        private boolean isTeacher = true;
        private OptionsPickerView mPickerView;

        private final ArrayList<String> datas = new ArrayList<>();
        private BottomCancelPopup mConfirm;

        SettingViewHolder(View view) {
            super(view);
            bitOptions.add(lowest);
            bitOptions.add(lower);
            bitOptions.add(medium);
            bitOptions.add(higher);
            bitOptions.add(highest);
            initOptionsPicker();

            datas.add("确定");
            initConfirmPopup();
        }

        private void initConfirmPopup() {
            mConfirm = new BottomCancelPopup(SettingActivity.this);
            mConfirm.setOutsideCancel(true);
            mConfirm.setKeyBackCancel(true);
            mConfirm.setChooseDatas(datas);
            mConfirm.setTip(kickTip);
            mConfirm.setIndexColor(0, Color.parseColor("#ff0000"));
            mConfirm.setOnChooseClickListener(new BottomCancelPopup.OnChooseClickListener() {
                @Override
                public void onClick(int index) {
                    allDown();
                }
            });
        }

        private void allDown() {
            showLoading();
            mInteractSession.allKickDownMai(new CCInteractSession.AtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                }

                @Override
                public void onFailure(String err) {
                    dismissLoading();
                    toastOnUiThread(err);
                }
            });
        }

        @OnClick(R.id.id_setting_down_mai)
        void allDownMai() {
            if (mInteractSession.isRoomLive()) {
                mConfirm.show(mRoot);
            } else {
                showToast("直播未开始");
            }
        }

        @OnClick(R.id.id_setting_media_mode)
        void mediaSetting() {
            SetSettingActivity.startSelf(SettingActivity.this, mMediaMode == MEDIA_MODE_AUDIO ?
                    0 : 1, SetSettingActivity.SETTING_MODE_MEDIA);
        }

        @OnClick(R.id.id_setting_lianmai_mode)
        void lianmaiSetting() {
            SetSettingActivity.startSelf(SettingActivity.this, mLianmaiMode == LIANMAI_MODE_FREE ?
                    0 : mLianmaiMode == LIANMAI_MODE_AUTO ? 2 : 1, SetSettingActivity.SETTING_MODE_LIANMAI);
        }

        @OnClick(R.id.id_setting_loop_play_time)
        void maxLianmai() {
            go(LoopTimeActivity.class);
        }

//        @OnClick(R.id.id_setting_bitrate)
//        void setBitrate() {
//            go(BitrateActivity.class);
//        }

        private void writeValue2PickIndex(int bitrate) {
            switch (bitrate) {
                case CCInteractSession.Bitrate_Level1:
                    mPickIndex = 0;
                    break;
                case CCInteractSession.Bitrate_Level2:
                    mPickIndex = 1;
                    break;
                case CCInteractSession.Bitrate_Level3:
                    mPickIndex = 2;
                    break;
                case CCInteractSession.Bitrate_Level4:
                    mPickIndex = 3;
                    break;
                case CCInteractSession.Bitrate_Level5:
                    mPickIndex = 4;
                    break;

            }
        }

        private void showPick(int bitrate) {
            writeValue2PickIndex(bitrate);
            mPickerView.setSelectOptions(mPickIndex);
            mPickerView.show();
        }

        @OnClick(R.id.id_setting_tbitrate)
        void tBitrate() {
            isTeacher = true;
            showPick(mInteractSession.getPresenterBitrate());
        }

        @OnClick(R.id.id_setting_sbitrate)
        void sBitrate() {
            isTeacher = false;
            showPick(mInteractSession.getTalkerBitrate());
        }

        private void initOptionsPicker() {
            mPickerView = new OptionsPickerView.Builder(SettingActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    if (isTeacher) {
                        commitTeacherbit(bitValues[options1]);
                    } else {
                        commitStudentbit(bitValues[options1]);
                    }
                }
            })
                    .setTitleText(mBitrateTip)
                    .setContentTextSize(20)//设置滚轮文字大小
                    .setDividerColor(getResources().getColor(R.color.colorPrimary))//设置分割线的颜色
                    .setBgColor(Color.BLACK)
                    .setTitleBgColor(Color.DKGRAY)
                    .setTitleColor(Color.LTGRAY)
                    .setCancelColor(getResources().getColor(R.color.colorPrimary))
                    .setSubmitColor(getResources().getColor(R.color.colorPrimary))
                    .setTextColorCenter(Color.LTGRAY)
                    .setBackgroundId(0x66000000) //设置外部遮罩颜色
                    .build();
            mPickerView.setPicker(bitOptions);
        }

        private void commitStudentbit(int level) {
            showLoading();
            mInteractSession.changeRoomStudentBitrate(level, new CCInteractSession.AtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                    mViewHolder.mSBitrate.setValue(mBitrateTips.get(mInteractSession.getTalkerBitrate()));
                }

                @Override
                public void onFailure(String err) {
                    dismissLoading();
                    toastOnUiThread(err);
                }
            });
        }

        private void commitTeacherbit(int level) {
            showLoading();
            mInteractSession.changeRoomTeacherBitrate(level, new CCInteractSession.AtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                    mViewHolder.mTBitrate.setValue(mBitrateTips.get(mInteractSession.getPresenterBitrate()));
                }

                @Override
                public void onFailure(String err) {
                    dismissLoading();
                    toastOnUiThread(err);
                }
            });
        }

    }

}
