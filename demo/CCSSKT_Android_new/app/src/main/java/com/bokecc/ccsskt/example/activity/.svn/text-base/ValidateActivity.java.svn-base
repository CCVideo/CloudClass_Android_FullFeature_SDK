package com.bokecc.ccsskt.example.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.popup.TxtLoadingPopup;
import com.bokecc.ccsskt.example.view.EllipsizeEndTextView;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.CCCityInteractBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bokecc.ccsskt.example.CCApplication.mAreaCode;
import static com.bokecc.ccsskt.example.CCApplication.mFisrtCityName;
import static com.bokecc.sskt.CCInteractSession.INSPECTOR;
import static com.bokecc.sskt.CCInteractSession.TALKER;

public class ValidateActivity extends TitleActivity<ValidateActivity.ValidateViewHolder> {

    private static final String KEY_ROOM_NAME = "room_name";
    private static final String KEY_ROOM_DESC = "room_desc";
    private static final String KEY_ROOM_ID = "room_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_STUDENT_LOGIN_NO_PWD = "student_login_no_pwd";
    public static final String KEY_APP_PLAY_URL = "app_playurl";//暖场视频播放地址
    private String mNickName;
    private String mPwd = "";

    private static Intent newIntent(Context context, String roomName, String roomDesc, String roomId, String userId, int role, boolean isNoPwd) {
        Intent intent = new Intent(context, ValidateActivity.class);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        intent.putExtra(KEY_ROOM_DESC, roomDesc);
        intent.putExtra(KEY_ROOM_ID, roomId);
        intent.putExtra(KEY_USER_ID, userId);
        intent.putExtra(KEY_ROLE, role);
        intent.putExtra(KEY_STUDENT_LOGIN_NO_PWD, isNoPwd);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static void startSelf(Context context, String roomName, String roomDesc, String roomId, String userId, int role, boolean isNoPwd) {
        context.startActivity(newIntent(context, roomName, roomDesc, roomId, userId, role, isNoPwd));
    }

    public static final String TAG = ValidateActivity.class.getSimpleName();

    private String mRoomId;
    private String mUserId;
    private int mRole;
    private boolean isNoPwd;

    private TxtLoadingPopup mLoading;
    private InputMethodManager imm;

    public volatile boolean isJoin = false, isJump = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_validate;
    }

    @Override
    protected ValidateViewHolder getViewHolder(View contentView) {
        return new ValidateViewHolder(contentView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Config.CITYNAME) {
            CCCityInteractBean mCityName = (CCCityInteractBean) data.getSerializableExtra
                    ("selected_city");
            mAreaCode = mCityName.getdataareacode();
            mFisrtCityName = mCityName.getdataloc();
            final TitleOptions.Builder builder = new TitleOptions.Builder();
            final Intent intent = new Intent(this, CityListActivity.class);
            TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                    leftResId(R.drawable.title_back).
                    rightResId(R.drawable.layer_list).
                    rightStatus(TitleOptions.VISIBLE).
                    title("验证").
                    cityStatus(TitleOptions.VISIBLE).city(mFisrtCityName).
                    onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                        @Override
                        public void onLeft() {
                            finish();
                        }
                        @Override
                        public void onRight() {
                            intent.putExtra("mRoomid", getIntent().getStringExtra(KEY_ROOM_ID));
                            intent.putExtra("mUserid", getIntent().getStringExtra(KEY_USER_ID));
                            startActivityForResult(intent,Config.CITYSTATUS_RESULT_CODE);
                        }
                    }).

                    build();
            setTitleOptions(options);
        }
    }

    @Override
    protected void onBindViewHolder(ValidateViewHolder holder) {

        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }

        final TitleOptions.Builder builder = new TitleOptions.Builder();
        final Intent intent = new Intent(this, CityListActivity.class);
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightResId(R.drawable.layer_list).
                rightStatus(TitleOptions.VISIBLE).
                title("验证").
                cityStatus(TitleOptions.VISIBLE).city(mFisrtCityName).
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                    @Override
                    public void onRight() {
                        intent.putExtra("mRoomid", getIntent().getStringExtra(KEY_ROOM_ID));
                        intent.putExtra("mUserid", getIntent().getStringExtra(KEY_USER_ID));
                        startActivityForResult(intent,Config.CITYSTATUS_RESULT_CODE);
                    }
                }).

                build();
        setTitleOptions(options);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        String roomName = getIntent().getStringExtra(KEY_ROOM_NAME);
        String roomDesc = getIntent().getStringExtra(KEY_ROOM_DESC);
        isNoPwd = getIntent().getBooleanExtra(KEY_STUDENT_LOGIN_NO_PWD, false);
        mRoomId = getIntent().getStringExtra(KEY_ROOM_ID);
        mUserId = getIntent().getStringExtra(KEY_USER_ID);
        mRole = getIntent().getIntExtra(KEY_ROLE, mRole);
        mNickName = mSPUtil.getString(mRoomId + "&" + mUserId + "&" + mRole + "-name");
        holder.mRoomName.setText(Html.fromHtml(roomName));
        if (!TextUtils.isEmpty(roomDesc)) {
            holder.mRoomDesc.setMaxLines(2);
            holder.mRoomDesc.setText(Html.fromHtml(roomDesc));
        } else {
            holder.mRoomDesc.setVisibility(View.GONE);
        }
        holder.mNickname.setText(mNickName);
        holder.mNickname.setSelection(mNickName.length());
        if (isNoPwd) {
            holder.mPwdLayout.setVisibility(View.GONE);
            holder.mPwdBottomLine.setVisibility(View.GONE);
        } else {
            mPwd = mSPUtil.getString(mRoomId + "&" + mUserId + "&" + mRole + "-password");
            holder.mPwdLayout.setVisibility(View.VISIBLE);
            holder.mPwdBottomLine.setVisibility(View.VISIBLE);
            holder.mPassword.setText(mPwd);
            holder.mPassword.setSelection(mPwd.length());
        }

        initLoadingPopup();
        showSoftboard();
    }

    @Override
    protected void onDestroy() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
        super.onDestroy();
    }

    private Runnable mShowSoftboardRunnable = new Runnable() {
        @Override
        public void run() {
            imm.showSoftInput(mViewHolder.mNickname, 0);
        }
    };

    public void showSoftboard() {
        mHandler.postDelayed(mShowSoftboardRunnable, 150);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_SERVER_CONNECT:
                synchronized (ValidateActivity.this) {
                    if (isJoin && !isJump) {
                        isJump = true;
                        dismissLoadingOnUiThread();
                        if (mRole == CCInteractSession.PRESENTER) {
                            go(TeacherActivity.class);
                        } else if(mRole == CCInteractSession.TALKER){
                            go(StudentActivity.class);
                        } else {
                            go(InspectorActivity.class);
                        }
                    }
                }
                break;
        }
    }

    private void join() {
        mInteractSession.joinRoom(new CCInteractSession.AtlasCallBack<String>() {
            @Override
            public void onSuccess(String aVoid) {
                synchronized (ValidateActivity.this) {
                    mSPUtil.put(KEY_APP_PLAY_URL, aVoid);
                    isJoin = true;
                    if (CCApplication.isConnect && !isJump) {
                        isJump = true;
                        dismissLoadingOnUiThread();
                        if (mRole == CCInteractSession.PRESENTER) {
                            go(TeacherActivity.class);
                        } else if(mRole == CCInteractSession.TALKER){
                            go(StudentActivity.class);
                        } else {
                            go(InspectorActivity.class);//隐身者
                        }
                    }
                }
            }

            @Override
            public void onFailure(String err) {
                dismissLoadingOnUiThread();
                toastOnUiThread(err);
            }
        });
    }

    private void saveUserMsg() {
        mSPUtil.put(mRoomId + "&" + mUserId + "&" + mRole + "-name", mNickName);
        if (!isNoPwd)
            mSPUtil.put(mRoomId + "&" + mUserId + "&" + mRole + "-password", mPwd);
    }

    private void initLoadingPopup() {
        mLoading = new TxtLoadingPopup(this);
        mLoading.setTipValue("正在登录");
        mLoading.setOutsideCancel(false);
        mLoading.setKeyBackCancel(false);
    }

    private void dismissLoadingOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoading.dismiss();
            }
        });
    }

    final class ValidateViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_validate_room_icon)
        ImageView mIcon;
        @BindView(R.id.id_validate_room_name)
        TextView mRoomName;
        @BindView(R.id.id_validate_room_desc)
        EllipsizeEndTextView mRoomDesc;
        @BindView(R.id.id_validate_nickname)
        EditText mNickname;
        @BindView(R.id.id_validate_passwd_layout)
        LinearLayout mPwdLayout;
        @BindView(R.id.id_validate_passwd_bottom_line)
        View mPwdBottomLine;
        @BindView(R.id.id_validate_passwd)
        EditText mPassword;

        ValidateViewHolder(View view) {
            super(view);
        }

        private String getEditTextValue(EditText editText) {
            return editText.getText().toString().trim();
        }

        @OnClick(R.id.id_validate_room)
        void goRoom() {
            mNickName = getEditTextValue(mNickname);
            mPwd = getEditTextValue(mPassword);
            if (mRole == TALKER && isNoPwd ) {
                if (TextUtils.isEmpty(mNickName)) {
                    toastOnUiThread("请填写登录信息");
                    return;
                }
            } else if(mRole == INSPECTOR && isNoPwd ){
                if (TextUtils.isEmpty(mNickName)) {
                    toastOnUiThread("请填写登录信息");
                    return;
                }
            } else {
                if (TextUtils.isEmpty(mNickName) || TextUtils.isEmpty(mPwd)) {
                    toastOnUiThread("请填写登录信息");
                    return;
                }
            }
            mLoading.show(mRoot);
            isJoin = false;
            isJump = false;
            mInteractSession.login(mRoomId,
                    mUserId, mRole, mNickName, mPwd, mAreaCode, new CCInteractSession
                            .OnLoginStatusListener
                            () {
                        @Override
                        public void onSuccess() {
                            saveUserMsg();
                            join();
                        }

                        @Override
                        public void onFailed(String err) {
                            dismissLoadingOnUiThread();
                            toastOnUiThread(err);
                        }
                    });
        }

    }

}
