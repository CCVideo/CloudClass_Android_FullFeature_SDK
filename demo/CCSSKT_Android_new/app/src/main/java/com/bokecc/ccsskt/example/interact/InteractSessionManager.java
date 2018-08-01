package com.bokecc.ccsskt.example.interact;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.entity.ChatEntity;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.SubscribeRemoteStream;
import com.bokecc.sskt.bean.ChatMsg;
import com.bokecc.sskt.bean.User;
import com.bokecc.sskt.bean.Vote;
import com.bokecc.sskt.bean.VoteResult;
import com.bokecc.sskt.doc.DocInfo;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class InteractSessionManager {

    private EventBus mEventBus;
    private CCInteractSession mInteractSession;
    private static InteractSessionManager instance;

    public static InteractSessionManager getInstance() {
        if (instance == null) {
            synchronized (InteractSessionManager.class) {
                if (instance == null) {
                    instance = new InteractSessionManager();
                }
            }
        }
        return instance;
    }

    private InteractSessionManager() {
        mInteractSession = CCInteractSession.getInstance();
        addInteractListeners();
    }

    public void setEventBus(EventBus eventBus) {
        mEventBus = eventBus;
    }

    /**
     * 聊天监听事件
     */
    private CCInteractSession.OnChatListener mChatListener = new CCInteractSession.OnChatListener() {
        @Override
        public void onReceived(User from, ChatMsg msg, boolean self) {
            final ChatEntity chatEntity = new ChatEntity();
            chatEntity.setType(msg.getType());
            chatEntity.setUserId(from.getUserId());
            chatEntity.setUserName(from.getUserName());
            chatEntity.setMsg(msg.getMsg());
            chatEntity.setTime(msg.getTime());
            chatEntity.setSelf(self);
            chatEntity.setUserRole(from.getUserRole());
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CHAT, chatEntity, self));
        }

        @Override
        public void onError(String err) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ERROR, err));
        }
    };
    /**
     * 获取人数 包含总人数和旁听人数 的监听事件
     */
    private CCInteractSession.OnUserCountUpdateListener mUserCountUpdateListener = new CCInteractSession.OnUserCountUpdateListener() {

        @Override
        public void onUpdate(int classCount, int audienceCount) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_COUNT, classCount, audienceCount));
        }
    };
    //直播间设置监听事件
    private CCInteractSession.OnUserListUpdateListener mUserListUpdateListener = new CCInteractSession.OnUserListUpdateListener() {
        @Override
        public void onUpdate(final ArrayList<User> users) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_LIST, users));
        }
    };
    /**
     * 学生被禁言监听事件
     *
     */
    private CCInteractSession.OnGagOneListener mGagOneUpdateListener = new CCInteractSession.OnGagOneListener() {
        @Override
        public void onGagOne(String userid, boolean isAllowChat) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_GAG, userid, isAllowChat));
        }
    };
    /**
     * 学生授权标注监听事件
     */
    private CCInteractSession.OnAuthDrawListener mAuthDrawListener = new CCInteractSession.OnAuthDrawListener() {
        @Override
        public void onAuth(String userid, boolean isAllowDraw) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_AUTH_DRAW, userid, isAllowDraw));
        }
    };
    /**
     * 设为讲师监听事件
     */
    private CCInteractSession.OnSetupTeacherListener mSetupTeacher = new CCInteractSession.OnSetupTeacherListener() {

        @Override
        public void onSetupTeacher(String userid, boolean isAllowDraw) {//设为讲师
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SETUP_THEACHER, userid, isAllowDraw));
        }

        @Override
        public void onChangePage(DocInfo mDocInfo, int position) {//设为讲师以后翻页事件的监听
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SETUP_THEACHER_PAGE, mDocInfo,position));
        }

        @Override
        public void onDocChange(DocInfo mDocInfo,int position) {//设为讲师以后文档加载监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOC_CHANGE, mDocInfo,position));
        }
    };
    /**
     * 学生麦克风状态被动变化监听事件
     */
    private CCInteractSession.OnAudioListener mAudioListener = new CCInteractSession.OnAudioListener() {
        @Override
        public void onAudio(String userid, boolean isAllowAudio, boolean isSelf) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_AUDIO, userid, isAllowAudio, isSelf));
        }
    };
    /**
     * 学生摄像头状态被动变化监听事件
     */
    private CCInteractSession.OnVideoListener mVideoListener = new CCInteractSession.OnVideoListener() {
        @Override
        public void onVideo(String userid, boolean isAllowVideo, boolean isSelf) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_VIDEO, userid, isAllowVideo, isSelf));
        }
    };
    /**
     * 老师调用全体禁言，学生接收监听事件
     */
    private CCInteractSession.OnGagAllListener mGagAllListener = new CCInteractSession.OnGagAllListener() {
        @Override
        public void onGag(boolean isAllowChat) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ALL_GAG, isAllowChat));
        }
    };
    /**
     * 学生被老师提出房间监听事件
     *
     */
    private CCInteractSession.OnKickOutListener mKickOutListener = new CCInteractSession.OnKickOutListener() {
        @Override
        public void onKickOut() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_KICK_OUT));
        }
    };
    /**
     * 学生排麦麦序更新监听事件
     */
    private CCInteractSession.OnQueueMaiUpdateListener mQueueMaiUpdateListener = new CCInteractSession.OnQueueMaiUpdateListener() {
        @Override
        public void onUpdate(ArrayList<User> users) {//排麦的回调
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_QUEUE_MAI, users));
        }
    };
    /**
     * 学生排麦状态监听事件
     */
    private CCInteractSession.OnNotifyMaiStatusLisnter mNotifyMaiStatusLisnter = new CCInteractSession.OnNotifyMaiStatusLisnter() {
        @Override
        public void onUpMai(int oldStatus) {//上麦事件监听
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_UP_MAI, oldStatus));
        }

        @Override
        public void onDownMai() {//下麦事件监听
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOWN_MAI));
        }
    };
    /**
     * 连麦多媒体模式变化回调
     * <p>
     */
    private CCInteractSession.OnMediaModeUpdateListener mMediaModeUpdateListener = new CCInteractSession.OnMediaModeUpdateListener() {
        @Override
        public void onUpdate(@CCInteractSession.MediaMode int mode) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_UPDATE_MEDIA_MODE, mode));
        }
    };
    /**
     * 连麦模式更新回调
     * <p>
     */
    private CCInteractSession.OnLianmaiModeUpdateListener mLianmaiModeUpdateListener = new CCInteractSession.OnLianmaiModeUpdateListener() {
        @Override
        public void onUpdate(@CCInteractSession.LianmaiMode int mode) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_UPDATE_LIANMAI_MODE, mode));
        }
    };
    /**
     * 流变化通知
     */
    private CCInteractSession.OnNotifyStreamListener mNotifyStreamListener = new CCInteractSession.OnNotifyStreamListener() {
        @Override
        public void onStreamAdded(SubscribeRemoteStream remoteStream) {//获取新的流回调
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_ADDED, remoteStream));
        }

        @Override
        public void onStreamRemoved(SubscribeRemoteStream remoteStream) {//移除流回调
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_REMOVED, remoteStream));
        }

        @Override
        public void onStreamError() {//流发生错误
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_ERROR));
        }
    };
    /**
     * 收到教师点名回调
     */
    private CCInteractSession.OnReceiveNamedListener mStartRollCallListener = new CCInteractSession.OnReceiveNamedListener() {
        @Override
        public void onReceived(int namedTime) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_START_NAMED, namedTime));
        }
    };
    /**
     * 教师开始点名回调
     *
     */
    private CCInteractSession.OnStartNamedListener mRollCallListListener = new CCInteractSession.OnStartNamedListener() {
        @Override
        public void onStartNamedResult(boolean isAllow, ArrayList<String> ids) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROLL_CALL_LIST, isAllow, ids));
        }
    };
    /**
     * 学生应答点名回调
     */
    private CCInteractSession.OnAnswerNamedListener mAnswerRollCallListener = new CCInteractSession.OnAnswerNamedListener() {
        @Override
        public void onAnswered(String answerUserId, ArrayList<String> answerIds) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ANSWER_NAMED, answerUserId, answerIds));
        }
    };
    /**
     * 监听直播间的状态事件
     */
    private CCInteractSession.OnServerListener mServerDisconnectListener = new CCInteractSession.OnServerListener() {
        @Override
        public void onDisconnect(int platform) {//直播间断开
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SERVER_DISCONNECT, platform));
        }

        @Override
        public void onConnect() {//直播间连接上
            CCApplication.isConnect = true;
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SERVER_CONNECT));
        }

        @Override
        public void onReconnect() {

        }

        @Override
        public void onReconnecting() {

        }
    };
    /**
     * 学生收到老师上麦邀请回调
     * <p>
     */
    private CCInteractSession.OnNotifyInviteListener mNotifyInviteListener = new CCInteractSession.OnNotifyInviteListener() {
        @Override
        public void onInvite() {//被邀请监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INVITE));
        }

        @Override
        public void onCancel() {//取消邀请连接事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INVITE_CANCEL));
        }
    };
    /**
     * 上课状态变化通知
     * <p>
     */
    private CCInteractSession.OnClassStatusListener mClassStatusListener = new CCInteractSession.OnClassStatusListener() {
        @Override
        public void onStart() {//开始上课事件通知
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CLASS_STATUS_START));
        }

        @Override
        public void onStop() {//下课事件通知
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CLASS_STATUS_STOP));
        }
    };
    /**
     * 主视频模式下，主视频更新回调
     * <p>
     */
    private CCInteractSession.OnFollowUpdateListener mFollowUpdateListener = new CCInteractSession.OnFollowUpdateListener() {
        @Override
        public void onFollow(String userid) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_MAIN_VIDEO_FOLLOW, userid));
        }
    };
    /**
     * 模板更新回调
     * <p>
     */
    private CCInteractSession.OnTemplateTypeUpdateListener mTemplateTypeUpdateListener = new CCInteractSession.OnTemplateTypeUpdateListener() {
        @Override
        public void onTemplateUpdate(@CCInteractSession.Template int template) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_TEMPLATE, template));
        }
    };
    /**
     * 教师异常下线回调
     * <p>
     */
    private CCInteractSession.OnTeacherDownListener mTeacherDownListener = new CCInteractSession.OnTeacherDownListener() {
        @Override
        public void onTeacherDown() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_TEACHER_DOWN));
        }
    };
    /**
     * 房间计时器回调
     * <p>
     */
    private CCInteractSession.OnRoomTimerListener mRoomTimerListener = new CCInteractSession.OnRoomTimerListener() {
        @Override
        public void onTimer(long startTime, long lastTime) {//开始计时
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_TIMER_START, startTime, lastTime));
        }

        @Override
        public void onStop() {//停止计时
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_TIMER_STOP));
        }
    };
    /**
     * 答题回调
     * <p>
     */
    private CCInteractSession.OnRollCallListener mRollCallListener = new CCInteractSession.OnRollCallListener() {
        @Override
        public void onStart(Vote vote) {//开始答题
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_START, vote));
        }

        @Override
        public void onStop(String voteId) {//停止答题
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_STOP, voteId));
        }

        @Override
        public void onResult(VoteResult voteResult) {//答题结果
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_RESULT, voteResult));
        }
    };
    /**
     * 设置举手回调
     *
     */
    private CCInteractSession.OnHandupListener mHandupListener = new CCInteractSession.OnHandupListener() {
        @Override
        public void onHandup(String userid, boolean isHandup) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_HANDUP, userid, isHandup));
        }
    };
    /**
     * 设置锁定回调
     *
     */
    private CCInteractSession.OnLockListener mLockListener = new CCInteractSession.OnLockListener() {
        @Override
        public void onLock(String userid, boolean isLock) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_LOCK, userid, isLock));
        }
    };
    /**
     * 老师拉学生上麦，而学生不具备上麦条件回调
     */
    private CCInteractSession.OnRecivePublishError mRecivePublishError = new CCInteractSession.OnRecivePublishError() {
        @Override
        public void onError(String userid, String username) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DEVICE_FAIL, userid, username));
        }
    };
    /**
     * 设置插播音视频回调
     */
    private CCInteractSession.OnInterludeMediaListener mInterludeMediaListener = new CCInteractSession.OnInterludeMediaListener() {
        @Override
        public void onInterlude(JSONObject object) {
            try {
                String handler = object.getString("handler");
                mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INTERLUDE_MEDIA, object));
            } catch (Exception ignored) {}
        }
    };
    /**
     * 老师设学生为讲师，学生翻页，老师也需要监听
     */
    private CCInteractSession.OnTeacherSetupTeacherListener mTeacherSetupPageListener = new CCInteractSession.OnTeacherSetupTeacherListener() {
        @Override
        public void onTeacherSetupTeacher(int position) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_TEACHER_SETUPTHEACHER_FLAG, position));
        }
    };
    /**
     * 设置视频放大到文档区回调方法
     */
    private CCInteractSession.OnVideoControlListener mVideoControlListener = new CCInteractSession.OnVideoControlListener() {

        @Override
        public void OnVideoControl(String userid,String type) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_VIDEO_CONTROL, userid,type));
        }
    };
    /**
     * 暖场视频取消，开始监听
     */
    private CCInteractSession.OnInterWramMediaListener mOnInterWramMediaListener = new CCInteractSession.OnInterWramMediaListener() {
        @Override
        public void onInterWram(Object object) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_WARM_VIDEO, object));
        }
    };

    /**
     * 监听当前文档
     */
    private CCInteractSession.OnPageChangeListener mOnPageChangeListener = new CCInteractSession.OnPageChangeListener() {
        @Override
        public void onPageChange(int position, boolean issdk) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_PAGECHANGE, position,issdk));
        }
    };
//    /**
//     * 流中断监听事件
//     */
//    private CCInteractSession.OnPublishBeakOffListener mOnInterruptPublishListener = new CCInteractSession.OnPublishBeakOffListener() {
//
//        @Override
//        public void onPublishBeakOff(JSONObject object) {
//            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INTERRUPT_PUBLISH, object));
//        }
//
//        @Override
//        public void onNotifyPublish(String streamid, String userid) {
//            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_NOTIFY_PUBLISH,streamid,userid));
//        }
//    };

    private void addInteractListeners() {
        mInteractSession.setOnChatListener(mChatListener);
        mInteractSession.setOnUserCountUpdateListener(mUserCountUpdateListener);
        mInteractSession.setOnUserListUpdateListener(mUserListUpdateListener);
        mInteractSession.setOnGagAllListener(mGagAllListener);
        mInteractSession.setOnGagOneListener(mGagOneUpdateListener);
        mInteractSession.setOnAuthDrawListener(mAuthDrawListener);
        mInteractSession.setOnSetupTeacher(mSetupTeacher);
        mInteractSession.setOnTeacherSetupPageListener(mTeacherSetupPageListener);
        mInteractSession.setOnAudioListener(mAudioListener);
        mInteractSession.setOnVideoListener(mVideoListener);
        mInteractSession.setOnQueueMaiUpdateListener(mQueueMaiUpdateListener);
        mInteractSession.setOnNotifyMaiStatusLisnter(mNotifyMaiStatusLisnter);
        mInteractSession.setOnKickOutListener(mKickOutListener);
        mInteractSession.setOnMediaModeUpdateListener(mMediaModeUpdateListener);
        mInteractSession.setOnLianmaiModeUpdateListener(mLianmaiModeUpdateListener);
        mInteractSession.setOnNotifyStreamListener(mNotifyStreamListener);
        mInteractSession.setOnReceiveNamedListener(mStartRollCallListener);
        mInteractSession.setOnStartNamedListener(mRollCallListListener);
        mInteractSession.setOnAnswerNamedListener(mAnswerRollCallListener);
        mInteractSession.setOnServerListener(mServerDisconnectListener);
        mInteractSession.setOnNotifyInviteListener(mNotifyInviteListener);
        mInteractSession.setOnClassStatusListener(mClassStatusListener);
        mInteractSession.setOnFollowUpdateListener(mFollowUpdateListener);
        mInteractSession.setOnTemplateTypeUpdateListener(mTemplateTypeUpdateListener);
        mInteractSession.setOnTeacherDownListener(mTeacherDownListener);
        mInteractSession.setOnRoomTimerListener(mRoomTimerListener);
        mInteractSession.setOnRollCallListener(mRollCallListener);
        mInteractSession.setOnHandupListener(mHandupListener);
        mInteractSession.setOnLockListener(mLockListener);
        mInteractSession.setOnRecivePublishError(mRecivePublishError);
        mInteractSession.setOnInterludeMediaListener(mInterludeMediaListener);
        mInteractSession.setOnVideoControlListener(mVideoControlListener);
        mInteractSession.setOnInterWramMediaListener(mOnInterWramMediaListener);
        mInteractSession.setOnPageChangeListener(mOnPageChangeListener);
//        mInteractSession.setOnInterruptPublishListener(mOnInterruptPublishListener);
    }

    public void reset() {
        instance = null;
    }

}
