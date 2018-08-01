package com.bokecc.ccsskt.example.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.VideoStreamView;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.recycle.RecycleViewDivider;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.base.exception.StreamException;
import com.bokecc.sskt.base.renderer.CCSurfaceRenderer;
import com.bumptech.glide.Glide;

import org.webrtc.RendererCommon;

import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MainVideoFragment extends BaseFragment {

    private static final String TAG = MainVideoFragment.class.getSimpleName();

    public MainVideoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRole == CCInteractSession.PRESENTER) {
            mTeacherInteractionListener.showFollow();
            mTeacherInteractionListener.dismissVideoController();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main_video;
    }

    @Override
    protected void transformData() { // 转换数据
        int position = -1;
        for (int i = 0; i < mVideoStreamViews.size(); i++) {
            VideoStreamView videoStreamView = mVideoStreamViews.get(i);
            if (TextUtils.isEmpty(mInteractSession.teacherFollowUserID())) {
                mPreMainVideoView = mMainVideoView;
                mMainVideoView = videoStreamView;
                position = 0;
                break;
            } else {
                if (videoStreamView.getStream().getUserId().equals(mInteractSession.teacherFollowUserID())) {
                    mPreMainVideoView = mMainVideoView;
                    mMainVideoView = videoStreamView;
                    position = i;
                    break;
                }
            }
        }
        if (position != -1) {
            mVideoStreamViews.remove(position);
        } else { // 没有找到跟随的视频 切换教师到主视图
            if (mVideoStreamViews.size() > 0) {
                mMainVideoView = mVideoStreamViews.get(0);
                mVideoStreamViews.remove(0);
            }
        }
    }

    @Override
    protected void setUpView() {
        mMainVideoTop.setVisibility(View.VISIBLE);
        if (mRole == CCInteractSession.TALKER) {
            mMainVideoTop.setEnabled(true);
            mMainVideoTop.setClickable(true);
            mMainVideoLayout.setEnabled(false);
            mMainVideoLayout.setClickable(false);
        } else {
            mMainVideoTop.setEnabled(false);
            mMainVideoTop.setClickable(false);
            mMainVideoLayout.setEnabled(true);
            mMainVideoLayout.setClickable(true);
        }
        mVideos.setLayoutManager(new LinearLayoutManager(mActivity));
        mVideos.setAdapter(mVideoAdapter);
        mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                0, 0, RecycleViewDivider.TYPE_BETWEEN));
        mVideos.addOnItemTouchListener(new BaseOnItemTouch(mVideos,
                new OnClickListener() {
                    @Override
                    public void onClick(RecyclerView.ViewHolder viewHolder) {
                        int position = mVideos.getChildAdapterPosition(viewHolder.itemView);
                        VideoStreamView videoStreamView = mVideoStreamViews.get(position);
                        if (mRole == CCInteractSession.TALKER) {
                            // 学生并且教师点击了跟随
                            if (!TextUtils.isEmpty(mInteractSession.teacherFollowUserID())) {
                                showToast("跟随模式下不能切换视频");
                                return;
                            }
                            // 切换大小屏
                            updateFollowId(videoStreamView, position);
                        } else {
                            if (mVideoStreamViews.get(position).getStream().getUserRole() == CCInteractSession.PRESENTER) {
                                // 切换大小屏
                                updateFollowId(videoStreamView, position);
                            } else {
                                if (mVideoClickListener != null) {
                                    mVideoClickListener.onVideoClick(position, videoStreamView);
                                }
                            }
                        }
                    }
                }));
        if (mTeacherInteractionListener != null) {
            mTeacherInteractionListener.updateFollow();
        }
        displayMainVideo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mMainVideoView != null && mMainVideoRenderer != null) {
                if (mMainVideoView.getStream().isLocalCameraStream()) {
                    mInteractSession.detachLocalCameraStram(mMainVideoRenderer);
                } else {
                    mMainVideoView.getStream().detach(mMainVideoRenderer);
                }
            }
        } catch (StreamException ignore) {
        } finally {
            mMainVideoView = null;
            mPreMainVideoView = null;
            if (mMainVideoRenderer != null) {
                mMainVideoRenderer.cleanFrame();
                mMainVideoRenderer.release();
                mMainVideoRenderer = null;
            }
        }
    }

    public void updateFollowId(final VideoStreamView videoStreamView, final int position) {
        // 老师并且设置了跟随
        if (mRole == CCInteractSession.PRESENTER && !TextUtils.isEmpty(mInteractSession.teacherFollowUserID())) {
            showLoading();
            mInteractSession.changeMainStreamInSigleTemplate(videoStreamView.getStream().getUserId(), new CCInteractSession.AtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                    if (position < 0 || position >= mVideoStreamViews.size()) {
                        return;
                    }
                    switchMainVideo(videoStreamView, position);
                }

                @Override
                public void onFailure(String err) {
                    dismissLoading();
                    toastOnUiThread(err);
                }
            });
        } else {
            switchMainVideo(videoStreamView, position);
        }
    }

    /**
     * 切换主视图
     */
    private void switchMainVideo(final VideoStreamView videoStreamView, final int position) {
        if (mRole == CCInteractSession.PRESENTER) {
            if (videoStreamView.getStream().getUserRole() == CCInteractSession.PRESENTER) {
                mInteractSession.setRegion(mInteractSession.getLocalStreamId());
            } else {
                mInteractSession.setRegion(videoStreamView.getStream().getStreamId());
            }
        }
        mPreMainVideoView = mMainVideoView;
        mMainVideoView = videoStreamView;
        mVideoStreamViews.set(position, mPreMainVideoView);
        mVideoAdapter.update(position, mPreMainVideoView);
        displayMainVideo();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mVideos;
    }

    public String getMainVideoUserid() {
        if (mMainVideoView != null) {
            return mMainVideoView.getStream().getUserId();
        } else {
            return null;
        }
    }

    public void findMainVideoByUseridToDisplay(String userid) {
        if (TextUtils.isEmpty(userid) || (mMainVideoView != null && mMainVideoView.getStream().getUserId().equals(userid))) {
            return;
        }
        for (int i = 0; i < mVideoStreamViews.size(); i++) {
            VideoStreamView videoStreamView = mVideoStreamViews.get(i);
            if (videoStreamView.getStream().getUserId().equals(userid)) {
                switchMainVideo(videoStreamView, i);
                break;
            }
        }
    }

    public void updateMainVideo(String userid, boolean flag, int changed) {
        if (mMainVideoView == null || !userid.equals(mMainVideoView.getStream().getUserId())) {
            return;
        }
        if (changed == 0) { // audio
            mMainVideoView.getStream().setAllowAudio(flag);
            if (flag) {
                Glide.with(mActivity).load(R.drawable.mic_open_icon).into(mMicClose);
            } else {
                Glide.with(mActivity).load(R.drawable.mic_close_icon).into(mMicClose);
            }
        } else if (changed == 1) {
            mMainVideoView.getStream().setAllowVideo(flag);
            mMainVideoRenderer.cleanFrame();
            if (CCApplication.sClassDirection == 0) {
                mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg);
            } else {
                mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg_land);
            }
            mOtherLayout.setVisibility(flag ? View.GONE : View.VISIBLE);
        } else if (changed == 2) {
            mDrawIcon.setVisibility(flag ? View.VISIBLE : View.GONE);
        } else if (changed == 3) {
            if (mInteractSession.getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
                mLockIcon.setVisibility(flag ? View.VISIBLE : View.GONE);
            } else {
                mLockIcon.setVisibility(View.GONE);
            }
        } else if (changed == 4) {
            mSetUpTeacherIcon.setVisibility(flag ? View.VISIBLE : View.GONE);//设为讲师
        }

        // 学生仅音频模式
        if (mMainVideoView.getStream().getUserRole() != CCInteractSession.PRESENTER &&
                CCInteractSession.getInstance().getMediaMode() == CCInteractSession.MEDIA_MODE_AUDIO) {
            if (mMainVideoView.getStream().getUserId().equals(CCInteractSession.SHARE_SCREEN_STREAM_ID)) {
                mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
            } else {
                mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                if (CCApplication.sClassDirection == 0) {
                    mOtherLayout.setBackgroundResource(R.drawable.only_mic_bg);
                } else {
                    mOtherLayout.setBackgroundResource(R.drawable.only_mic_bg_land);
                }
            }
        } else {
            if (!mMainVideoView.getStream().isAllowVideo()) { // 关闭视频
                mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                if (CCApplication.sClassDirection == 0) {
                    mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg);
                } else {
                    mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg_land);
                }
            } else {
                mOtherLayout.setVisibility(View.GONE);
            }
        }
    }

    public void animateTop(int top) {
        if (mMainVideoTop != null) {
            mMainVideoTop.animate().translationYBy(top).setDuration(100).start();
        }
    }

    @Override
    public void classStop() {
        mMainVideoView = null;
    }

    @Override
    public synchronized void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd) {
        if (isAdd) {
            if (mMainVideoView != null && videoStreamView.getStream().getUserId().equals(mMainVideoView.getStream().getUserId())) {
                return;
            }
            for (VideoStreamView temp :
                    mVideoStreamViews) {
                if (temp.getStream().getUserId().equals(videoStreamView.getStream().getUserId())) {
                    return;
                }
            }
        }
        // 1. 判断跟随
        // 6. 有教师 显示教师
        // 7. 没有教师显示第一个
        // 因为教师默认在第一个所以可以直接显示第一个
        if (TextUtils.isEmpty(mInteractSession.teacherFollowUserID())) { // 2. 如果没有跟随
            if (isAdd) {
                if (position == 0) { // 如果是第一个
                    setMainViewAndUpdateVideos(videoStreamView, true);
                } else { // 不是第一个
                    updateVideoList(videoStreamView, true); // 更新列表
                }
            } else {
                // 如果当前被移除的视图是主视图
                if (mMainVideoView != null && videoStreamView.getStream().getUserId().equals(mMainVideoView.getStream().getUserId())) {
                    setMainViewAndUpdateVideos(videoStreamView, false);
                } else {
                    updateVideoList(videoStreamView, false);
                }
            }
        } else { // 3. 如果有跟随
            // 当前被操纵的视图不是当前跟随的视图
            if (!videoStreamView.getStream().getUserId().equals(mInteractSession.teacherFollowUserID())) { // 不是
                if (isAdd) { // 如果是添加视图
                    if (position == 0) { // 第一个
                        if (mMainVideoView == null) { // 主视频没有直接添加
                            mPreMainVideoView = null;
                            mMainVideoView = videoStreamView; // 获取当前视图上面的流
                            displayMainVideo(); // 添加到主视图
                        } else { // 主视频有 当前添加的是老师，
                            // 主视频显示的不是当前的跟随视频 更新主视频为老师视频
                            if (!mMainVideoView.getStream().getUserId().equals(mInteractSession.teacherFollowUserID())) {
                                if (videoStreamView.getStream().getUserRole() == CCInteractSession.PRESENTER) {
                                    setMainViewAndUpdateVideos(videoStreamView, true);
                                }
                            } else { // 主视频显示的是跟随视频，添加老师到列表
                                updateVideoList(videoStreamView, true);
                            }
                        }
                    } else {
                        updateVideoList(videoStreamView, true); // 更新列表
                    }
                } else {
                    // 如果当前被移除的视图是主视图
                    if (mMainVideoView != null && videoStreamView.getStream().getUserId().equals(mMainVideoView.getStream().getUserId())) {
                        setMainViewAndUpdateVideos(videoStreamView, false);
                    } else {
                        updateVideoList(videoStreamView, false);
                    }
                }
            } else { // 当前操作的视图是跟随视图
                setMainViewAndUpdateVideos(videoStreamView, isAdd);
            }
        }
    }

    @Override
    public void notifyHandUp() {
        if (mMainVideoTop != null) {
            mMainVideoTop.setEnabled(false);
            mDisplayInteractionListener.toggleTopAndBottom();
        }
    }

    private void setMainViewAndUpdateVideos(VideoStreamView videoStreamView, boolean isAdd) {
        mPreMainVideoView = mMainVideoView;
        if (isAdd) { // 添加的是主视频
            mMainVideoView = videoStreamView;
            if (mPreMainVideoView != null) { // 将上一个主视频移回列表
                updateVideoList(mPreMainVideoView, true);
            }
            displayMainVideo();
        } else { // 移除主视频
            if (mVideoStreamViews != null && mVideoStreamViews.size() > 0) { // 列表还有数据
                mMainVideoView = mVideoStreamViews.get(0); // 默认获取第一个作为主视频
                for (VideoStreamView temp :
                        mVideoStreamViews) { // 遍历列表 如果有老师存在 主视频显示老师
                    if (temp.getStream().getUserRole() == CCInteractSession.PRESENTER) {
                        mMainVideoView = temp;
                        break;
                    }
                }
                updateVideoList(mMainVideoView, false); // 将主视频显示的从列表移除
                displayMainVideo();
            } else {
                try {
                    if (mPreMainVideoView != null && mMainVideoRenderer != null) {
                        if (mPreMainVideoView.getStream().isLocalCameraStream()) {
                            mInteractSession.detachLocalCameraStram(mMainVideoRenderer);
                        } else {
                            mPreMainVideoView.getStream().detach(mMainVideoRenderer);
                        }
                    }
                } catch (StreamException e) {
                    if (mMainVideoRenderer != null) {
                        mMainVideoRenderer.cleanFrame();
                        mMainVideoRenderer.release();
                        mMainVideoRenderer = null;
                    }
                } finally {
                    mPreMainVideoView = null;
                    mMainVideoView = null;
                }
            }
        }
    }

    private void displayMainVideo() {
        try {
            if (!isViewInitialize || mMainVideoView == null) {
                return;
            }
            if (mPreMainVideoView != null && mMainVideoRenderer != null) { // 移除视图上面的旧数据
                if (mPreMainVideoView.getStream().isLocalCameraStream()) {
                    mInteractSession.detachLocalCameraStram(mMainVideoRenderer);
                } else {
                    mPreMainVideoView.getStream().detach(mMainVideoRenderer);
                }
            }
            if (mMainVideoRenderer == null) {
                mMainVideoRenderer = new CCSurfaceRenderer(mActivity);
                RelativeLayout.LayoutParams params;
                if (CCApplication.sClassDirection == 0) {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mMainVideoRenderer.setLayoutParams(params);
                mInteractSession.initSurfaceContext(mMainVideoRenderer);
                mMainVideoRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                mSurfaceContainer.addView(mMainVideoRenderer);
            }
            mMainVideoRenderer.setZOrderOnTop(false);
            mMainVideoRenderer.setZOrderMediaOverlay(false);
            mMainVideoRenderer.cleanFrame();
            if (mMainVideoView.getStream().isLocalCameraStream()) { // 当前用户角色是教师，主视频显示自己的预览
                mInteractSession.attachLocalCameraStram(mMainVideoRenderer);
            } else {
                mMainVideoRenderer.setMirror(false);
                mMainVideoView.getStream().attach(mMainVideoRenderer);
            }
            mMainVideoName.setText(mMainVideoView.getStream().getUserName());
            mMicClose.setVisibility(View.VISIBLE);
            if (mMainVideoView.getStream().isAllowAudio()) {
                Glide.with(mActivity).load(R.drawable.mic_open_icon).into(mMicClose);
            } else {
                Glide.with(mActivity).load(R.drawable.mic_close_icon).into(mMicClose);
            }
            if (mMainVideoView.getStream().getUserName().equals(CCInteractSession.SHARE_SCREEN_STREAM_NAME)) {
                mMicClose.setVisibility(View.GONE);
            }
            mDrawIcon.setVisibility(mMainVideoView.getStream().isAllowDraw() ? View.VISIBLE : View.GONE);
            //设为讲师
            mSetUpTeacherIcon.setVisibility(mMainVideoView.getStream().isSetupTeacher() ? View.VISIBLE : View.GONE);

            if (mInteractSession.getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
                mLockIcon.setVisibility(mMainVideoView.getStream().isLock() ? View.VISIBLE : View.GONE);
            } else {
                mLockIcon.setVisibility(View.GONE);
            }
            // 学生仅音频模式
            if (mMainVideoView.getStream().getUserRole() != CCInteractSession.PRESENTER &&
                    CCInteractSession.getInstance().getMediaMode() == CCInteractSession.MEDIA_MODE_AUDIO) {
                if (mMainVideoView.getStream().getUserId().equals(CCInteractSession.SHARE_SCREEN_STREAM_ID)) {
                    mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
                } else {
                    mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                    if (CCApplication.sClassDirection == 0) {
                        mOtherLayout.setBackgroundResource(R.drawable.only_mic_bg);
                    } else {
                        mOtherLayout.setBackgroundResource(R.drawable.only_mic_bg_land);
                    }
                }
            } else {
                if (!mMainVideoView.getStream().isAllowVideo()) { // 关闭视频
                    mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                    if (CCApplication.sClassDirection == 0) {
                        mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg);
                    } else {
                        mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg_land);
                    }
                } else {
                    mOtherLayout.setVisibility(View.GONE);
                    if (mMainVideoView.getStream().getRemoteStream() != null) {
                        if (!mMainVideoView.getStream().getRemoteStream().hasAudio()) {
                            mMicClose.setVisibility(View.VISIBLE);
                            Glide.with(mActivity).load(R.drawable.no_mic_icon).into(mMicClose);
                        }
                        if (!mMainVideoView.getStream().getRemoteStream().hasVideo()) {
                            mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                            if (CCApplication.sClassDirection == 0) {
                                mOtherLayout.setBackgroundResource(R.drawable.no_camera_icon);
                            } else {
                                mOtherLayout.setBackgroundResource(R.drawable.no_camera_icon_land);
                            }
                        }
                    }
                }
            }
//            mTopUserInfoLayout.bringToFront();
        } catch (StreamException e) {
            mPreMainVideoView = null;
            if (mMainVideoRenderer != null) {
                mMainVideoRenderer.cleanFrame();
                mMainVideoRenderer.release();
                mMainVideoRenderer = null;
            }
        }
    }

    private synchronized void updateVideoList(VideoStreamView videoStreamView, boolean isAdd) {
        if (mVideoStreamViews == null) {
            return;
        }
        int index;
        if (isAdd) {
            index = mVideoStreamViews.size();
            mVideoStreamViews.add(videoStreamView);
        } else {
            index = mVideoStreamViews.indexOf(videoStreamView);
            mVideoStreamViews.remove(videoStreamView);
        }
        if (index == -1 || !isViewInitialize) {
            return;
        }
        notifyLayoutManagerRefresh();
        if (isAdd) {
            mVideoAdapter.notifyItemInserted(index);
        } else {
            mVideoAdapter.notifyItemRemoved(index);
        }
        if (index != mVideoStreamViews.size() - 1) {
            mVideoAdapter.notifyItemRangeChanged(index, mVideoStreamViews.size() - index);
        }
    }

    public void restoreClick() {
        if (mMainVideoTop != null) {
            mMainVideoTop.setEnabled(true);
        }
    }

    public CopyOnWriteArrayList<VideoStreamView> getDatas() {
        return mVideoStreamViews;
    }

    @OnClick(R.id.id_main_video_top_content)
    void animateTopAndBottom() {
        if (mMainVideoTop != null) {
            mMainVideoTop.setEnabled(false);
            mDisplayInteractionListener.toggleTopAndBottom();
        }
    }

    @OnClick(R.id.id_main_video_layout)
    void showPopup() {
        if (mMainVideoView.getStream().getUserRole() == CCInteractSession.TALKER && mVideoClickListener != null) {
            mVideoClickListener.onVideoClick(-1, mMainVideoView);
        }
    }

    private VideoStreamView mPreMainVideoView;
    private VideoStreamView mMainVideoView;

    @BindView(R.id.id_main_video_little_videos)
    RecyclerView mVideos;
    @BindView(R.id.id_main_video_layout)
    RelativeLayout mMainVideoLayout;
    @BindView(R.id.id_main_video_top_content)
    RelativeLayout mMainVideoTop;
    @BindView(R.id.id_main_video_user_info)
    LinearLayout mTopUserInfoLayout;
    @BindView(R.id.id_main_video_name)
    TextView mMainVideoName;
    CCSurfaceRenderer mMainVideoRenderer;
    @BindView(R.id.id_main_video_container)
    RelativeLayout mSurfaceContainer;
    @BindView(R.id.id_main_video_layout_camera_close)
    RelativeLayout mOtherLayout;
    @BindView(R.id.id_main_video_mic_close)
    ImageView mMicClose;
    @BindView(R.id.id_main_video_draw)
    ImageView mDrawIcon;
    @BindView(R.id.id_main_video_lock)
    ImageView mLockIcon;
    @BindView(R.id.id_main_video_setup_theacher)
    ImageView mSetUpTeacherIcon;
    public static MainVideoFragment newInstance(int role) {
        Bundle args = new Bundle();
        args.putInt(KEY_PARAM_ROLE, role);
        MainVideoFragment fragment = new MainVideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
