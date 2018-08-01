package com.bokecc.ccsskt.example.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.HeaderAndFooterWrapper;
import com.bokecc.ccsskt.example.adapter.RoomUserAdapter;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.entity.RoomUser;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.recycle.RecycleViewDivider;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.ccsskt.example.util.RoomUserComparator;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.User;
import com.bokecc.sskt.bean.Vote;
import com.bokecc.sskt.bean.VoteResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;

public class ListActivity extends TitleActivity<ListActivity.ListViewHolder> {

    private static final String TAG = ListActivity.class.getSimpleName();

    private ArrayList<RoomUser> mRoomUsers;

    private static Intent newIntent(Context context, int type, int count) {
        Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("count", count);
        return intent;
    }

    public static void startSelf(Context context, int type, int count) {
        context.startActivity(newIntent(context, type, count));
    }

    // 当前的用户角色
    private int mType = CCInteractSession.PRESENTER;
    private int mCount;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;

    private TextView mFootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected int getContentLayoutId() {
        return R.layout.activity_list;
    }

    @Override
    protected ListViewHolder getViewHolder(View contentView) {
        return new ListViewHolder(contentView);
    }

    @Override
    protected void beforeSetContentView() {
        if (CCApplication.sClassDirection == 1) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
    protected void onBindViewHolder(ListViewHolder holder) {
        mRoomUsers = new ArrayList<>();

        mFootView = new TextView(this);
        mFootView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mFootView.setGravity(Gravity.CENTER);
        int padding = DensityUtil.dp2px(this, 10);
        mFootView.setTextColor(Color.BLACK);
        mFootView.setPadding(padding, padding, padding, padding);

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mType = getIntent().getIntExtra("type", mType);
        mCount = getIntent().getIntExtra("count", mCount);

        setTitle(mCount + "个成员");
        ArrayList<User> users = mInteractSession.getUserList();
        transformUser(users);
        RoomUserAdapter roomUserAdapter = new RoomUserAdapter(this, mType,
                mInteractSession.getLianmaiMode());
        roomUserAdapter.bindDatas(mRoomUsers);
        holder.mRoomUserList.setLayoutManager(new LinearLayoutManager(this));
        holder.mRoomUserList.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, 1, Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BOTTOM));
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(roomUserAdapter);
        holder.mRoomUserList.setAdapter(mHeaderAndFooterWrapper);
        baseOnRoleSetAction(holder.mRoomUserList);
        loopUserCount();
    }

    private void transformUser(ArrayList<User> users) {
        if (users == null || users.size() <= 0) {
            return;
        }
        mRoomUsers.clear();
        if (mHeaderAndFooterWrapper != null) {
            mHeaderAndFooterWrapper.notifyDataSetChanged();
        }
        ArrayList<RoomUser> compareUsers = new ArrayList<>();
        int index = 0;
        for (User user :
                users) {
            if (mUserPopup.isShowing()) { // 如果弹出框显示，更新当前选中的用户状态
                if (mCurUser != null && user.getUserId().equals(mCurUser.getUserId())) {
                    updateOrShowUserPopup(user);
                }
            }
            RoomUser roomUser = new RoomUser();
            roomUser.setUser(user);
            if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IN_MAI ||
                    user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_UP_MAI) {
                compareUsers.add(roomUser);
            } else {
                if (user.getUserRole() == CCInteractSession.PRESENTER) {
                    mRoomUsers.add(0, roomUser);
                    index++;
                } /*else if (user.getLianmaiStatus() == 3) { // 这段代码实现将连麦中的学生位置定位在老师的下面
                    mRoomUsers.add(index, roomUser);
                    index++;
                }*/ else {
                    mRoomUsers.add(roomUser);
                }
            }
        }
        Collections.sort(compareUsers, new RoomUserComparator());
        int queueIndex = 1;
        for (RoomUser queueUser :
                compareUsers) {
            queueUser.setMaiIndex(queueIndex++);
        }
        mRoomUsers.addAll(index, compareUsers);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_USER_GAG:
                String userid = (String) event.obj;
                for (RoomUser roomUser :
                        mRoomUsers) {
                    if (roomUser.getUser().getUserId().equals(userid)) {
                        roomUser.getUser().getUserSetting().setAllowChat((Boolean) event.obj2);
                    }
                }
                updateList(); // 更新列表
                break;
            case Config.INTERACT_EVENT_WHAT_USER_AUDIO:
                userid = (String) event.obj;
                for (RoomUser roomUser :
                        mRoomUsers) {
                    if (roomUser.getUser().getUserId().equals(userid)) {
                        roomUser.getUser().getUserSetting().setAllowAudio((Boolean) event.obj2);
                    }
                }
                updateList(); // 更新列表
                break;
            case Config.INTERACT_EVENT_WHAT_AUTH_DRAW:
                userid = (String) event.obj;
                for (RoomUser roomUser :
                        mRoomUsers) {
                    if (roomUser.getUser().getUserId().equals(userid)) {
                        roomUser.getUser().getUserSetting().setAllowDraw((Boolean) event.obj2);
                    }
                }
                updateList(); // 更新列表
                break;
            case Config.INTERACT_EVENT_WHAT_SETUP_THEACHER:
                userid = (String) event.obj;
                for (RoomUser roomUser :
                        mRoomUsers) {
                    if (roomUser.getUser().getUserId().equals(userid)) {
                        roomUser.getUser().getUserSetting().setSetupTeacher((Boolean) event.obj2);
                    }
                }
                updateList(); // 更新列表
                break;
            case Config.INTERACT_EVENT_WHAT_HANDUP:
                userid = (String) event.obj;
                for (RoomUser roomUser :
                        mRoomUsers) {
                    if (roomUser.getUser().getUserId().equals(userid)) {
                        roomUser.getUser().getUserSetting().setHandUp((Boolean) event.obj2);
                    }
                }
                updateList(); // 更新列表
                break;
            case Config.INTERACT_EVENT_WHAT_USER_LIST:
            case Config.INTERACT_EVENT_WHAT_QUEUE_MAI:
                ArrayList<User> users = (ArrayList<User>) event.obj; // 重新赋值
                transformUser(users);
                updateList(); // 更新列表
                break;
            case Config.INTERACT_EVENT_WHAT_USER_COUNT:
                setTitle(((Integer) event.obj + (Integer) event.obj2) + "个成员");
                int audienceCount = (int) event.obj2;
                if (audienceCount > 0) {
                    if (mHeaderAndFooterWrapper.getFootersCount() <= 0) {
                        // 添加foot
                        mHeaderAndFooterWrapper.addFootView(mFootView);
                        mHeaderAndFooterWrapper.notifyDataSetChanged();
                    }
                    // 更新显示的数据
                    mFootView.setText("当前有" + audienceCount + "位旁听学生");
                } else {
                    if (mHeaderAndFooterWrapper.getFootersCount() > 0) {
                        mHeaderAndFooterWrapper.removeFootView(0);
                        mHeaderAndFooterWrapper.notifyDataSetChanged();
                    }
                }
                break;
            case Config.INTERACT_EVENT_WHAT_INVITE:
                if (isStop)
                    return;
                showInvite();
                break;
            case Config.INTERACT_EVENT_WHAT_INVITE_CANCEL:
                dismissInvite();
                break;
            case Config.INTERACT_EVENT_WHAT_START_NAMED:
                showNamed((Integer) event.obj);
                break;
            case Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_START:
                showVote((Vote) event.obj);
                break;
            case Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_STOP:
                dismissVote((String) event.obj);
                break;
            case Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_RESULT:
                showVoteResult((VoteResult) event.obj);
                break;
        }
    }

    private void updateList() {
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    private void baseOnRoleSetAction(final RecyclerView roomUserList) {
        if (mType == CCInteractSession.PRESENTER) {
            roomUserList.addOnItemTouchListener(new BaseOnItemTouch(roomUserList,
                    new OnClickListener() {
                        @Override
                        public void onClick(RecyclerView.ViewHolder viewHolder) {
                            int position = roomUserList.getChildAdapterPosition(viewHolder.itemView);
                            if (position == 0) {
                                return;
                            }
                            updateOrShowUserPopup(mRoomUsers.get(position).getUser());
                        }
                    }));
        }
    }

    final class ListViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_room_list)
        RecyclerView mRoomUserList;

        ListViewHolder(View view) {
            super(view);
        }
    }

}
