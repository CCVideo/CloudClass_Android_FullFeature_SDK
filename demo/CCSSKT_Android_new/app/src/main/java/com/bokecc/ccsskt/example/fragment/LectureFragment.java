package com.bokecc.ccsskt.example.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.activity.DocImgGridActivity;
import com.bokecc.ccsskt.example.adapter.ColorAdapter;
import com.bokecc.ccsskt.example.base.BaseActivity;
import com.bokecc.ccsskt.example.bridge.OnDocInteractionListener;
import com.bokecc.ccsskt.example.bridge.OnTeacherLectureListener;
import com.bokecc.ccsskt.example.entity.ColorStatus;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.entity.VideoStreamView;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.DividerGridItemDecoration;
import com.bokecc.ccsskt.example.recycle.MyLinearlayoutManager;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.recycle.RecycleViewDivider;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.ccsskt.example.view.DocView;
import com.bokecc.ccsskt.example.view.DocWebView;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.SubscribeRemoteStream;
import com.bokecc.sskt.base.exception.StreamException;
import com.bokecc.sskt.base.renderer.CCSurfaceRenderer;
import com.bokecc.sskt.doc.DocInfo;
import com.bumptech.glide.Glide;
import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleRelativeLayout;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.github.rongi.rotate_layout.layout.RotateLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.RendererCommon;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bokecc.ccsskt.example.global.Config.KEY_DOC_POSITION;

public class LectureFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = LectureFragment.class.getSimpleName();
    private DocInfo mCurDocInfo, mPreDocInfo;

    private boolean isOnlyDoc = false;
    private boolean isDocFullScreen = false, isVideoFullScreen = false;
    private int mDocBottomDistance = -1;
    private boolean isCancelTask;
    private int change = -1;
    private boolean isFirstChangePage = false;

    private int mPageIndex = -1 ;
    private boolean isSDK;

    private OnDocInteractionListener mOnDocInteractionListener;

    public LectureFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDocInteractionListener) {
            mOnDocInteractionListener = (OnDocInteractionListener) context;
        }
        if (context instanceof OnTeacherLectureListener) {
            mOnTeacherLectureListener = (OnTeacherLectureListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRole == CCInteractSession.PRESENTER ) {
            mTeacherInteractionListener.dismissFollow();
            if(CCApplication.sClassDirection == 1){
                mTeacherInteractionListener.showVideoController();
            }
        }
    }

    @Override
    public void onStart() {
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
        super.onStart();
        executeDismissDocWidgets();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isOnlyDoc) {
            animateDocBottom(-mDocBottomDistance, false);
            mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        } else {
            cancelDismissDocTask();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFullScreenRenderer = null;
        if (mDocView != null) {
            mDocView.recycle();
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_lecture;
    }

    private int lastX;
    private int lastY;

    @Override
    protected void setUpView() {
        if (mRole == CCInteractSession.PRESENTER) {//老师端 进行横竖屏
            mDocProgress.setVisibility(View.GONE);
            mImgGrid.setVisibility(View.GONE);
            mDocWebView.setVisibility(View.VISIBLE);//ppt动画类
            mDocView.setVisibility(View.VISIBLE);//画板和文档类（图片，普通ppt）
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            if (isDocFullScreen || CCApplication.sClassDirection == 1) {
                mDocView.setTouchInterceptor(isAuth,mRole);//设置画笔颜色老师为红色，学生为蓝色
             
            }
            mInteractSession.setDocView(mDocView,mDocWebView);//设置文档视图样式
            mDocWebView.setDocSetVisibility(mDocView);
            mDocView.setDocSetVisibility(mDocWebView);
            mDocArea.setBackgroundColor(Color.parseColor("#ffffff"));
            if (CCApplication.sClassDirection == 0) {//竖屏模式
                mFullScreen.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.VISIBLE);//手机竖屏模式，承载视频框可见
                mHVideoLayout.setVisibility(View.GONE);////手机横屏模式，承载视频框不可见
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_v_videos);//视频展示（recycview控件）
                mVideos.setLayoutManager(new MyLinearlayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.VERTICAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            } else {//横屏模式。
                mFullScreen.setVisibility(View.GONE);
                mHVideoLayout.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.GONE);
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_h_videos);
                mVideos.setLayoutManager(new LinearLayoutManager(mActivity));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            }
        } else { // 学生 进行横竖屏
            mDocProgress.setVisibility(View.GONE);
            mImgGrid.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mDocWebView.setVisibility(View.VISIBLE);
            mDocView.setVisibility(View.VISIBLE);
            if (isDocFullScreen || CCApplication.sClassDirection == 1) {
                mDocView.setTouchInterceptor(isAuth,mRole);
              
            }
            mInteractSession.setDocView(mDocView,mDocWebView);
            mDocWebView.setDocSetVisibility(mDocView);
            mDocView.setDocSetVisibility(mDocWebView);
            mDocArea.setBackgroundColor(Color.parseColor("#ffffff"));
            if (CCApplication.sClassDirection == 0) {
                mFullScreen.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.VISIBLE);
                mHVideoLayout.setVisibility(View.GONE);
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_v_videos);
                mVideos.setLayoutManager(new MyLinearlayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.VERTICAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            } else {
                mFullScreen.setVisibility(View.GONE);
                mHVideoLayout.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.GONE);
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_h_videos);
                mVideos.setLayoutManager(new LinearLayoutManager(mActivity));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            }
        }

        initDrawPopup();//初始化画笔工具箱

        //初始化画笔工具框可以移动
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDrawLayout.getLayoutParams();
        mDrawLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取到手指处的横坐标和纵坐标
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指按下时
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE://手指移动时
                        //计算移动的距离
                        int offX = x - lastX;
                        int offY = y - lastY;
                        params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                        params.topMargin = params.topMargin + offY;
                        params.leftMargin = params.leftMargin + offX;
                        if (params.topMargin < 0) {
                            params.topMargin = 0;
                        }
                        if (params.leftMargin < 0) {
                            params.leftMargin = 0;
                        }
                        if (params.topMargin > (DensityUtil.getHeight(mActivity) - mDrawLayout.getHeight())) {
                            params.topMargin = DensityUtil.getHeight(mActivity) - mDrawLayout.getHeight();
                        }
                        if (params.leftMargin > (DensityUtil.getWidth(mActivity) - mDrawLayout.getWidth())) {
                            params.leftMargin = DensityUtil.getWidth(mActivity) - mDrawLayout.getWidth();
                        }
                        mDrawLayout.setLayoutParams(params);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;

                }
                return true;
            }
        });

        //初始化文档放置区域
        calDocArea();
        //视频适配器（Recycview）
        mVideos.setAdapter(mVideoAdapter);
        //监听视频框的触摸时间
        mVideos.addOnItemTouchListener(new BaseOnItemTouch(mVideos,
                new OnClickListener() {
                    @Override
                    public void onClick(RecyclerView.ViewHolder viewHolder) {
                        int position = mVideos.getChildAdapterPosition(viewHolder.itemView);
                        VideoStreamView videoStreamView = mVideoStreamViews.get(position);
                        if (mRole == CCInteractSession.TALKER) {//学生端
                            if (isClickVideoFullScreen) {
                                return;
                            }
                            isClickVideoFullScreen = true;
                            videoFullScreen(position);//放大视频框
                        } else {
                            if (videoStreamView.getStream().getUserRole() == CCInteractSession
                                    .PRESENTER) {//老师端
                                if (isClickVideoFullScreen) {
                                    return;
                                }
                                isClickVideoFullScreen = true;
                                videoFullScreen(position);//放大视频框
                            } else {
                                if (mVideoClickListener != null) {
                                    mVideoClickListener.onVideoClick(position, videoStreamView);
                                }
                            }
                        }
                    }
                }));
        restoreDoc();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mVideos;
    }

    //监听视频框的变化（RecyclerView框架处理）
    @Override
    public synchronized void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd) {
        if (mVideoStreamViews == null) {
            return;
        }
        if(position > mVideoStreamViews.size()){
            position = position - 1;
        }
        if (isAdd) {
            for (VideoStreamView temp :
                    mVideoStreamViews) {
                if (temp.getStream().getUserId().equals(videoStreamView.getStream().getUserId())) {
                    return;
                }
            }
//            if(position > mVideoStreamViews.size()) {
//                position = position -1;
//            }
            mVideoStreamViews.add(position, videoStreamView);
        } else {
            mVideoStreamViews.remove(videoStreamView);
        }
        if (!isViewInitialize) {
            return;
        }
        if (isAdd) {
            mVideoAdapter.notifyItemInserted(position);
        } else {
            mVideoAdapter.notifyItemRemoved(position);
        }
        if (position != mVideoStreamViews.size() - 1) {
            mVideoAdapter.notifyItemRangeChanged(position, mVideoStreamViews.size() - position);
        }
    }
    VideoStreamView mtempView;
    private int videoposition;
    private String studentMode;
    private String userid;
    public void isVideSale(VideoStreamView steam){
//        if(mFullScreenRenderer1 != null){
//            mFullScreenRenderer1 = null;
//        }
        if(mtempView!=null){
            mFullScreenRenderer1 = null;
            if (mtempView.getStream().getUserId().equals(steam.getStream().getUserId())) {
                setVideoScale(mtempView.getStream().getUserId(),"big");
            } else {
                return;
            }
        }
    }
    //双击视频放大到文档区
    public void videoFlah(int Position){
        if(studentMode!= null && studentMode.equals("big")){
            if(mtempView.getStream().getUserId().equals(userid)){
                notifyItemChanged(mtempView,Position - 1,false);
            }
        }
    }
    //老师双击视频放大到文档区(app端不支持双击视频放大到文档区，只有web端双击了，app监听事件，再放大到文档区)
    public void setVideoScale(String userid,String mode){
        VideoStreamView tempView = null;
        studentMode = mode;
        this.userid = userid;

        if(mode.equals("big")){
            if(mtempView != null){
                if(!mtempView.getStream().getUserId().equals(userid)){
                    notifyItemChanged(mtempView,videoposition,true);
                    exitdocvideo();
                }
                if (CCApplication.sClassDirection == 1) {
                    mHVideoLayout.setVisibility(View.GONE);
                }

            }
            for (int i = 0; i < mVideoStreamViews.size(); i++) {
                VideoStreamView streamView = mVideoStreamViews.get(i);
                if (streamView.getStream().getUserId().equals(userid)) {
                    tempView = streamView;
                    mtempView = streamView;
                    videoposition = i;
                    break;
                }
            }

            if (tempView == null){
                return;
            }
            mVideoLayout.setVisibility(View.VISIBLE);
            if(CCApplication.sClassDirection == 0){
                ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mVideoLayout.getLayoutParams());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
                int width = DensityUtil.getWidth(mActivity);
                layoutParams.height = width * 9 / 16;
                mVideoLayout.setLayoutParams(layoutParams);
            }
            if (mFullScreenRenderer1 == null) {
                mFullScreenRenderer1 = new CCSurfaceRenderer(mActivity);
                RelativeLayout.LayoutParams params;
                if (CCApplication.sClassDirection == 0) {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    mHVideoLayout.setVisibility(View.GONE);
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mFullScreenRenderer1.setLayoutParams(params);
                mFullScreenRenderer1.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                mFullScreenRenderer1.setZOrderOnTop(true);
                mFullScreenRenderer1.setZOrderMediaOverlay(true);
                mVideoSurfaceContainer.addView(mFullScreenRenderer1);
            }
            if (CCApplication.sClassDirection == 1) {
                mHVideoLayout.setVisibility(View.GONE);
            }
            mFullScreenRenderer1.setVisibility(View.VISIBLE);
            mFullScreenRenderer1.setBackgroundColor(Color.BLACK);
            mVideoSurfaceContainer.setVisibility(View.VISIBLE);
            if(mFullScreenRenderer1 != null) {
                mInteractSession.initSurfaceContext(mFullScreenRenderer1, mRendererEvents1);
            }
//            mFullScreenStream1 = mVideoInteractionListener.getStream(videoposition);
            mFullScreenStream1 = mVideoStreamViews.get(videoposition).getStream();
            try {
                if (!mFullScreenStream1.isLocalCameraStream()) {
                    mFullScreenRenderer1.setMirror(false);
                    mFullScreenStream1.attach(mFullScreenRenderer1);

                } else {
                    mInteractSession.attachLocalCameraStram(mFullScreenRenderer1);
                }
            } catch (StreamException e) {
                e.printStackTrace();
            }
            notifyItemChanged(tempView,videoposition,false);
            mVideoStreamViews.remove(tempView);
        } else if (mode.equals("small")){
            if(mtempView == null){
                return;
            } else {
                exitdocvideo();
            }
        }
    }
    //老师端再次双击的时候，将文档区的视频退回到原来的位置。
    private void exitdocvideo(){
        if(mtempView !=null && mtempView.getStream().getUserId().equals(userid)){
            notifyItemChanged(mtempView,videoposition,true);
        }
        if(mFullScreenRenderer1 == null){
            return;
        }
        mVideoLayout.setVisibility(View.GONE);
        mFullScreenRenderer1.setVisibility(View.GONE);
        try {
            if (mFullScreenStream1.isLocalCameraStream()) {
                mInteractSession.detachLocalCameraStram(mFullScreenRenderer1);
            } else {
                mFullScreenStream1.getRemoteStream().detach(mFullScreenRenderer1);
            }
            mFullScreenRenderer1.cleanFrame();
            mFullScreenRenderer1.release();
            mHVideoLayout.bringToFront();
            if (CCApplication.sClassDirection == 1) {
                mHVideoLayout.setVisibility(View.VISIBLE);
            }
            mVideoFullScreenLayout.bringToFront();
            if(mCurPosition >= 0 ){
//                if(!mCurDocInfo.isUseSDK()){
//                    isFirstChangePage = false;
                mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                sendPageChange2(mCurPosition);
//                }
            }
//            studentMode =null;
//            mtempView = null;

        } catch (StreamException ignored) {
            mFullScreenRenderer1 = null;
        }
    }
    //放大到文档区的视频，如果是被下麦了，去掉视频框
    private void downMaiexitVideo() {
        if(mtempView !=null && mtempView.getStream().getUserId().equals(userid)){
            notifyItemChanged(mtempView,videoposition,true);
        }
        if(mtempView !=null&&mtempView.getStream().getUserId().equals(mInteractSession.getUserIdInPusher())){
            if(mFullScreenStream1.isLocalCameraStream()){
                notifyItemChanged(mtempView,videoposition-1,false);
            }
        }

        if(mFullScreenRenderer1 == null){
            return;
        }
        mVideoLayout.setVisibility(View.GONE);
        mFullScreenRenderer1.setVisibility(View.GONE);
        try {
            if (mFullScreenStream1.isLocalCameraStream()) {
                mInteractSession.detachLocalCameraStram(mFullScreenRenderer1);
            } else {
                mFullScreenStream1.getRemoteStream().detach(mFullScreenRenderer1);
            }
            mFullScreenRenderer1.cleanFrame();
            mFullScreenRenderer1.release();
            mHVideoLayout.bringToFront();
            if (CCApplication.sClassDirection == 1) {
                mHVideoLayout.setVisibility(View.VISIBLE);
            }
            studentMode =null;
            mtempView = null;
        } catch (StreamException ignored) {
            mFullScreenRenderer1 = null;
        }
    }

    //举手通知事件
    @Override
    public void notifyHandUp() {
        mDocArea.setEnabled(false);
        mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        toggleDocBottomLayout();
    }
    //点击视频框（学生和老师都支持），放大视频
    public void videoFullScreen(int position) {
        try {
            if (isVideoFullScreen) {
                return;
            }
            if (!isOnlyDoc) {
                cancelDismissDocTask();
            }
            mOnDocInteractionListener.videoFullScreen();
            mVideoFullScreenLayout.setClickable(true);
            mVideoFullScreenLayout.setVisibility(View.VISIBLE);
            if (mFullScreenRenderer == null) {
                mFullScreenRenderer = new CCSurfaceRenderer(mActivity);
                RelativeLayout.LayoutParams params;
                if (CCApplication.sClassDirection == 0) {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mFullScreenRenderer.setLayoutParams(params);
                mFullScreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                mFullScreenRenderer.setZOrderOnTop(true);
                mFullScreenRenderer.setZOrderMediaOverlay(true);
                mSurfaceContainer.addView(mFullScreenRenderer);
            }
            mFullScreenRenderer.setVisibility(View.VISIBLE);
            mFullScreenRenderer.setBackgroundColor(Color.BLACK);
            mSurfaceContainer.setVisibility(View.VISIBLE);
            mInteractSession.initSurfaceContext(mFullScreenRenderer, mRendererEvents);
//            mVideoExit.bringToFront(); // 保证关闭全屏的控件始终在最上层
            mVideoFullScreenLayout.bringToFront();
            if(mFullScreenRenderer1 !=null){
                mVideoLayout.setVisibility(View.GONE);
                mFullScreenRenderer1.setVisibility(View.GONE);
            }
            if(mFullScreenRenderer1!= null && studentMode != null && studentMode.equals("big")){
                mFullScreenStream = mVideoStreamViews.get(position).getStream();
            } else {
                mFullScreenStream = mVideoInteractionListener.getStream(position);
            }
//            mFullScreenStream = mVideoInteractionListener.getStream(position);
            mMicClose.setVisibility(mFullScreenStream.isAllowAudio() ? View.GONE : View.VISIBLE);
            // 学生仅音频模式
            if (mFullScreenStream.getUserRole() != CCInteractSession.PRESENTER &&
                    CCInteractSession.getInstance().getMediaMode() == CCInteractSession.MEDIA_MODE_AUDIO) {
                if (mFullScreenStream.getUserId().equals(CCInteractSession.SHARE_SCREEN_STREAM_ID)) {
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
                if (!mFullScreenStream.isAllowVideo()) { // 关闭视频
                    mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                    if (CCApplication.sClassDirection == 0) {
                        mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg);
                    } else {
                        mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg_land);
                    }
                } else {
                    mOtherLayout.setVisibility(View.GONE);
                    if (mFullScreenStream.getRemoteStream() != null) {
                        if (!mFullScreenStream.getRemoteStream().hasAudio()) {
                            mMicClose.setVisibility(View.VISIBLE);
                            Glide.with(mActivity).load(R.drawable.no_mic_icon).into(mMicClose);
                        }
                        if (!mFullScreenStream.getRemoteStream().hasVideo()) {
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
            if (!mFullScreenStream.isLocalCameraStream()) {
                mFullScreenRenderer.setMirror(false);
                mFullScreenStream.attach(mFullScreenRenderer);
            } else {
                mInteractSession.attachLocalCameraStram(mFullScreenRenderer);
            }
            isVideoFullScreen = true;
        } catch (StreamException e) {
            mVideoFullScreenLayout.setVisibility(View.GONE);
            if (mFullScreenRenderer != null) {
                mFullScreenRenderer.setVisibility(View.GONE);
                mFullScreenRenderer.cleanFrame();
                mFullScreenRenderer = null;
            }

        }
    }
    //点击退出按钮，退出视频全屏。
    private void exitVideoFullScreen() {
        if(mFullScreenRenderer1!=null && studentMode != null && studentMode.equals("big")){
            mVideoLayout.setVisibility(View.VISIBLE);
            mFullScreenRenderer1.setVisibility(View.VISIBLE);
        }
        if (!isVideoFullScreen) {
            return;
        }
        isClickVideoFullScreen = false;
        if (!isOnlyDoc) {
            executeDismissDocWidgets();
        }
        mOnDocInteractionListener.exitVideoFullScreen();
        mVideoFullScreenLayout.setVisibility(View.GONE);
        mFullScreenRenderer.setVisibility(View.GONE);
        try {
            if (mFullScreenStream.isLocalCameraStream()) {
                mInteractSession.detachLocalCameraStram(mFullScreenRenderer);
            } else {
                mFullScreenStream.getRemoteStream().detach(mFullScreenRenderer);
            }
            mFullScreenRenderer.cleanFrame();
            mFullScreenRenderer.release();
            isVideoFullScreen = false;
        } catch (StreamException ignored) {
            mFullScreenRenderer = null;
        }
    }
    private boolean isAuthflag = false;

    //监听授权标注和设为讲师的状态
    public void authDraw(boolean isAuth,int flag) {
        this.isAuth = isAuth;
        this.change = flag;
        if (isDocFullScreen || CCApplication.sClassDirection == 1) {
            if (isViewInitialize) {
                mDocView.setTouchInterceptor(isAuth,mRole);
                if (isDocFullScreen) {
//                    mDrawLayout.setVisibility(isAuth ? View.VISIBLE : View.GONE);
                    if(flag == 2){//监听到授权标注
                        if(isAuth){
                            mDrawLayout.setVisibility(View.VISIBLE);
                        } else {
                            mDrawLayout.setVisibility(View.GONE);
                        }
                        mPageChangeLayout.setVisibility(View.GONE);
                        mClear.setVisibility(View.GONE);
                    } if (flag == 4){//监听到设为讲师事件
                        if(isAuth){
                            mDrawLayout.setVisibility(View.VISIBLE);
                            if (mCurDocInfo.getPageTotalNum() <= 0) {
                                if(mCurDocInfo.isSetupTeacher()&&mRole == CCInteractSession.TALKER){
                                    mPageChangeLayout.setVisibility(View.GONE);
                                }
                            } else {
                                if(mCurDocInfo.isSetupTeacher()&&mRole == CCInteractSession.TALKER){
                                    mPageChangeLayout.setVisibility(View.VISIBLE);
                                }
                            }
                            mClear.setVisibility(View.VISIBLE);
                        } else {
                            mDrawLayout.setVisibility(View.GONE);
                        }
                    }

                    if (mDrawLayout.getVisibility() == View.VISIBLE) {
//                        mEraser.setVisibility(View.GONE);
                        if(mRole == CCInteractSession.PRESENTER){
                            mPageChangeLayout.setVisibility(View.VISIBLE);
                        }

                    }
                }
                if (!isAuth) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    mDocView.reset();
                }
            }
        }
    }

    //设置画笔实线的宽度
    public void setStrokeWidth(float width) {
        mDocView.setStrokeWidth(width);
    }
    //设置画笔颜色
    public void setColor(int color, int colorStr) {
        mDocView.setColor(color, colorStr);
    }
    //设置画笔撤销
    public void undo() {
        mDocView.undo();
    }
    //设置画笔清除
    public void clear() {
        mDocView.clear();
    }
    //设置白板
    public boolean isWhitboard() {
        return mCurDocInfo == null;
    }

    public boolean dealWithFullscreen() {
        boolean flag = isVideoFullScreen || isDocFullScreen;
        exitVideoFullScreen();
        docExitFullScreen();
        mDocView.reset(); // 重置绘画板
        return flag;
    }
    // 如果是讲课模式 判断大屏
    public void updateFullScreenVideoIfShow(boolean flag, int changed) {
        if (!isVideoFullScreen)
            return;
        if (changed == 0) { // audio
            mMicClose.setVisibility(flag ? View.GONE : View.VISIBLE);
        } else if (changed == 1) {
            mOtherLayout.setVisibility(flag ? View.GONE : View.VISIBLE);
        }
    }
    // 判断大屏
    public boolean isDocFullScreen() {
        return isDocFullScreen;
    }

    private void calDocArea() {
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        int width = DensityUtil.getWidth(mActivity);
        int height = width * 9 / 16;

        if (CCApplication.sClassDirection == 0) {
            mDocView.setWhiteboard(width, height,true);
        } else {
            int max = Math.max(DensityUtil.getHeight(mActivity), width);
            int min = Math.min(DensityUtil.getHeight(mActivity), width);
            mDocView.setWhiteboard( DensityUtil.getRealHeight(mActivity).x, DensityUtil.getRealHeight(mActivity).y,true);
        }
        if (CCApplication.sClassDirection == 0) {
            params.height = height;
        } else {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mDocArea.setLayoutParams(params);
    }

    public void showDocLoading(final long total) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPrepareLayout.setVisibility(View.VISIBLE);
                mUpdateTip.setVisibility(View.VISIBLE);
                mPrepareBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void dismissDocLoading(final String docId, final boolean isSuccess) {
        mPreDocInfo = mCurDocInfo;
        mCurDocInfo = null;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    // 进行恢复文档
                    mLocalDocId = docId;
                    mLocalPosition = 0;
                    mSPUtil.put(Config.KEY_DOC_ID + Config.mRoomId, mLocalDocId);
                    mSPUtil.put(KEY_DOC_POSITION, mLocalPosition);
                    fetchRoomDoc(1);
                } else {
                    mPrepareLayout.setVisibility(View.GONE);
                    mPrepareBar.setVisibility(View.GONE);
                    mUpdateTip.setVisibility(View.GONE);
                }
            }
        });
    }

    public void restoreNormal() {
//        if (mRole == CCInteractSession.TALKER) {
//            mDocView.clear();
//        }
        mDocArea.setEnabled(true);
        executeDismissDocWidgets();
    }

    @Override
    public void classStop() {
        mDocView.clearAll();
        dealWithFullscreen();
        mDocArea.setEnabled(false);
        if (isOnlyDoc) {
            animateDocBottom(-mDocBottomDistance, false);
            mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        } else {
            cancelDismissDocTask();
        }
    }

    public void exitVideoFullScreenIfShow(SubscribeRemoteStream stream) {
        if(studentMode!=null && studentMode.equals("big")) {
            if (mVideoLayout.getVisibility() == View.VISIBLE) {
                if (mFullScreenStream1 != null && mFullScreenStream1.getUserId().equals(stream.getUserId())) {
                    downMaiexitVideo();
                }
            }
        } else {
            if (mVideoFullScreenLayout.getVisibility() == View.VISIBLE) {
                if (mFullScreenStream != null && mFullScreenStream.getUserId().equals(stream.getUserId())) {
                    exitVideoFullScreen();
                }
            }
        }
    }

    public void animateTop(int top) {
        if (mHVideoLayout != null) {
            mHVideoLayout.animate().translationYBy(top).setDuration(100).start();
        }
    }

    public void setOnlyDoc(boolean onlyDoc) {
        isOnlyDoc = onlyDoc;
    }

    private Runnable dismissDocWidgetsTask = new Runnable() {
        @Override
        public void run() {
            if (mDocArea == null || mDocBottomLayout == null) {
                return;
            }
            mDocArea.setEnabled(false);
            if (mDocBottomDistance == -1) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDocBottomLayout.getLayoutParams();
                mDocBottomDistance = params.bottomMargin + mDocBottomLayout.getHeight();
            }
            if (isCancelTask) {
                return;
            }
            if (CCApplication.sClassDirection == 0) {
                mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
            } else {
                mDisplayInteractionListener.toggleTopAndBottom();
            }
            animateDocBottom(mDocBottomDistance, false);
        }
    };

    private void executeDismissDocWidgets() {
        if (mRole == CCInteractSession.TALKER && !mInteractSession.isRoomLive()) {
            mDocArea.setEnabled(false);
            return;
        }
        isCancelTask = false;
        mHandler.postDelayed(dismissDocWidgetsTask, 3000);
    }

    private void cancelDismissDocTask() {
        isCancelTask = true;
        mHandler.removeCallbacks(dismissDocWidgetsTask);
    }

    private void animateDocBottom(int bottom, final boolean isDoDismissTask) {
        if (mDocBottomLayout == null) {
            return;
        }
        mDocBottomLayout.animate().cancel();
        mDocBottomLayout.animate().translationYBy(bottom).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                isOnlyDoc = !isOnlyDoc;
                if (mDocArea != null && (
                        mRole == CCInteractSession.PRESENTER ||
                                (mRole == CCInteractSession.TALKER && mInteractSession.isRoomLive())
                )) {
                    mDocArea.setEnabled(true);
                }
                if (isDoDismissTask) {
                    executeDismissDocWidgets();
                }
            }
        }).start();
    }

    private void toggleDocBottomLayout() {
        if (mDocBottomDistance == -1) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDocBottomLayout.getLayoutParams();
            mDocBottomDistance = params.bottomMargin + mDocBottomLayout.getHeight();
        }
        if (isOnlyDoc) {
            animateDocBottom(-mDocBottomDistance, true);
        } else {
            cancelDismissDocTask();
            animateDocBottom(mDocBottomDistance, false);
        }
    }

    @OnClick(R.id.id_lecture_doc_area)
    void toggleOperatingArea() {
        mDocArea.setEnabled(false);
        if (CCApplication.sClassDirection == 0) { // 竖屏
            mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        } else { // 横屏
            mDisplayInteractionListener.toggleTopAndBottom();
        }
        toggleDocBottomLayout();
    }

    @OnClick(R.id.id_lecture_doc_img_grid)
    void showDocImgGrid() {
        Bundle bundle = new Bundle();
        if (mCurDocInfo == null) {
            bundle.putStringArrayList("doc_img_list", null);
        } else {
            bundle.putStringArrayList("doc_img_list", mCurDocInfo.getAllImgUrls());
        }
        ((BaseActivity) mActivity).isGo = true;
        go(DocImgGridActivity.class, bundle, Config.LECTURE_REQUEST_CODE);
    }

    //点击放大文档按钮（在竖屏模式下才有这个按钮）
    @OnClick(R.id.id_lecture_doc_fullscreen)
    void docFullScreen() {
        if (isDocFullScreen) {
            return;
        }
        cancelDismissDocTask();
        mDocArea.setEnabled(false);
        mFullScreen.setVisibility(View.GONE);
        mExitFullScreen.setVisibility(View.VISIBLE);
        if (mRole == CCInteractSession.PRESENTER) {
            mImgGrid.setVisibility(View.GONE);
            mDocView.setTouchInterceptor(true,mRole);
            mDrawLayout.setVisibility(View.VISIBLE);
            mClear.setVisibility(View.VISIBLE);
            mDocForward.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            if (isWhitboard()) {
                mPageChangeLayout.setVisibility(View.GONE);
            } else {
                if(mCurDocInfo.getPageTotalNum() == 1) {
                    mPageChangeLayout.setVisibility(View.GONE);
                } else {
                    mPageChangeLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        if(mCurDocInfo != null){
            if(mCurDocInfo.isSetupTeacher()&&mRole == CCInteractSession.TALKER){
                mImgGrid.setVisibility(View.GONE);
                mDocView.setTouchInterceptor(true,mRole);
                mDrawLayout.setVisibility(View.VISIBLE);
                mClear.setVisibility(View.VISIBLE);
                mDocForward.setVisibility(View.GONE);
                mDocBack.setVisibility(View.GONE);
                if (mCurDocInfo.getPageTotalNum() <= 0) {
                    mPageChangeLayout.setVisibility(View.GONE);
                } else {
                    if (mCurDocInfo.getPageTotalNum() == 1) {
                        mPageChangeLayout.setVisibility(View.GONE);
                    } else {
                        mPageChangeLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        params.height = DensityUtil.getWidth(mActivity);
        mDocArea.setLayoutParams(params);
//        mDocRotate.setAngle(270);

        if(mPageIndex == -1){
            mDocView.setWhiteboard( DensityUtil.getRealHeight(mActivity).y, DensityUtil.getWidth
                    (mActivity),true);
        } else if(isSDK) {
            mDocView.setWhiteboard( DensityUtil.getRealHeight(mActivity).y, DensityUtil.getWidth
                    (mActivity),false
            );
        }
        mDocView.rotate(true);
        if (isAuth) { // 文档全屏如果被授权的时候 设置文档view可绘制
            mDocView.setTouchInterceptor(true,mRole);
            mDrawLayout.setVisibility(View.VISIBLE);
            if(change == 2){
                mClear.setVisibility(View.GONE);
            } else if(change == 4){
                mClear.setVisibility(View.VISIBLE);
            }
        } else {
            mDocView.setTouchInterceptor(false,mRole);
            mDrawLayout.setVisibility(View.GONE);
        }
        mOnDocInteractionListener.docFullScreen();
        RelativeLayout.LayoutParams params1;
        params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoLayout.setLayoutParams(params1);
        mOnDocInteractionListener.docFullScreen();
        isDocFullScreen = true;
    }

    //点击退出文档按钮（在竖屏模式下才有这个按钮）
    @OnClick(R.id.id_lecture_doc_exit_fullscreen)
    void docExitFullScreen() {
        if (!isDocFullScreen) {
            return;
        }
        mDocView.setTouchInterceptor(false,mRole);
        mDocView.rotate(false);
        mDrawLayout.setVisibility(View.GONE);
        executeDismissDocWidgets();
        mDocArea.setEnabled(true);
        mFullScreen.setVisibility(View.VISIBLE);
        mExitFullScreen.setVisibility(View.GONE);
        if (mRole == CCInteractSession.PRESENTER) {
            if (!isWhitboard()) {
                if(mCurDocInfo.getPageTotalNum() == 1){
                    mDocForward.setVisibility(View.GONE);
                    mDocBack.setVisibility(View.GONE);
                } else {
                    mImgGrid.setVisibility(View.VISIBLE);
                    if (mCurPosition == 0) {
                        mDocForward.setVisibility(View.VISIBLE);
                    } else if (mCurPosition == mCurDocInfo.getPageTotalNum() - 1) {
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.VISIBLE);
                    } else {
                        mDocForward.setVisibility(View.VISIBLE);
                        mDocBack.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        if(mCurDocInfo != null){
            if(mCurDocInfo.isSetupTeacher()&&mRole == CCInteractSession.TALKER){
                if (mCurDocInfo.getPageTotalNum() > 0) {
                    mPageChangeLayout.setVisibility(View.GONE);
                    if(mCurDocInfo.getPageTotalNum() == 1){
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        mImgGrid.setVisibility(View.VISIBLE);
                        if (mCurPosition == 0) {
                            mDocForward.setVisibility(View.VISIBLE);
                        } else if (mCurPosition == mCurDocInfo.getPageTotalNum() - 1) {
                            mDocForward.setVisibility(View.VISIBLE);
                            mDocBack.setVisibility(View.VISIBLE);
                        } else {
                            mDocForward.setVisibility(View.VISIBLE);
                            mDocBack.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    mDocForward.setVisibility(View.GONE);
                    mDocBack.setVisibility(View.GONE);
                    mImgGrid.setVisibility(View.GONE);
                }
            }
        }
        int width = DensityUtil.getRealHeight(mActivity).y;
        int height = width * 9 / 16;
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        params.height = height;
        mDocArea.setLayoutParams(params);

        if(mPageIndex == -1){
            mDocView.setWhiteboard(width, height,true);
        } else if(isSDK) {
            mDocView.setWhiteboard(width, height,false);
        }

//        mDocRotate.setAngle(0);
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mVideoLayout.getLayoutParams());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        int Videowidth = DensityUtil.getHeight(mActivity);
        layoutParams.height = Videowidth * 9 / 16;
        Log.i("wdh---。", "docExitFullScreen: " + Videowidth + " " + layoutParams.height);
        mVideoLayout.setLayoutParams(layoutParams);
        mOnDocInteractionListener.exitDocFullScreen();
        isDocFullScreen = false;
    }

    @OnClick(R.id.id_lecture_video_exit)
    void doExitVideoFullScreen() {
        exitVideoFullScreen();
    }

    //点击撤销画笔按钮
    @OnClick(R.id.id_lecture_draw_undo)
    void doUndo() {
        undo();
    }
    //点击清空画笔按钮
    @OnClick(R.id.id_lecture_draw_clear)
    void doClear() {
        clear();
    }
    @OnClick(R.id.id_lecture_draw_paint)
    void showPaint() {
        if ((mDrawLayout.getY() + mDrawLayout.getHeight() + 20 + mPopupView.getHeight()) >
                DensityUtil.getHeight(mActivity)) {
            mPopupWindow.showArrowTo(mDrawPaint, BubbleStyle.ArrowDirection.Down, 20);
        } else {
            mPopupWindow.showArrowTo(mDrawPaint, BubbleStyle.ArrowDirection.Up, 20);
        }
    }

    @OnClick(R.id.id_lecture_bar_doc_back)
    void barDocBack() {
        docBack();
    }

    @OnClick(R.id.id_lecture_bar_doc_forward)
    void barDocForward() {
        docForward();
    }

    //文档翻页的点击事件（返回翻页按钮）
    private int totalstep;
    private int stepnum = 0;
    @OnClick(R.id.id_lecture_doc_back)
    public void docBack() {
        if(mCurDocInfo.isUseSDK()){
            mDocWebView.evaluateJavascript("javascript:window.ANIMATIONSTEPSCOUNT", new
                    ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    if(s != null){
                        try{
                            totalstep = Integer.valueOf(s).intValue();
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                    }
                }
            });
            mDocWebView.evaluateJavascript("javascript:window.TRIGGERED_ANIMATION_STEP", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    if(s != null){
                        try{
                            stepnum = Integer.valueOf(s).intValue();
                            --stepnum;
                            if(stepnum < 0){
                                if(mCurPosition == 0){
                                    return;
                                } else{
                                    isFirstChangePage =false;
                                    mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(--mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                                    sendPageChange(mCurPosition);
                                }
                            } else {
                                mInteractSession.pptAnimationChange(mCurDocInfo.getDocId(), stepnum = 0, mCurPosition);

                            }
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                    }

                }
            });

        } else {
            if(mCurPosition > 0){
                isFirstChangePage =false;
                mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(--mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                sendPageChange(mCurPosition);
            }
        }
    }
    //文档翻页的点击事件（往前翻页按钮）
    @OnClick(R.id.id_lecture_doc_forward)
    public void docForward() {
            if(mCurDocInfo.isUseSDK()){
                mDocWebView.evaluateJavascript("javascript:window.ANIMATIONSTEPSCOUNT", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if(s != null){
                            try{
                                totalstep = Integer.valueOf(s).intValue();
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }

                        }
                    }
                });
                mDocWebView.evaluateJavascript("javascript:window.TRIGGERED_ANIMATION_STEP", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if(s != null){
                            try{
                                stepnum = Integer.valueOf(s).intValue();
                                ++stepnum;
                                if(stepnum >= totalstep ){
                                    if(mCurPosition == mCurDocInfo.getPageTotalNum() - 1){
                                        return;
                                    } else {
                                        isFirstChangePage =false;
                                        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(++mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                                        sendPageChange(mCurPosition);
                                    }
                                    stepnum = 0;
                                } else {
                                    mInteractSession.pptAnimationChange(mCurDocInfo.getDocId(), stepnum, mCurPosition);

                                }
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }

                        }
                    }
                });
            } else {
                isFirstChangePage =false;
                if(mCurPosition < mCurDocInfo.getPageTotalNum()-1){
                    mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(++mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                    sendPageChange(mCurPosition);
                }
            }
    }

    @BindView(R.id.id_lecture_doc_area)
    RelativeLayout mDocArea;
    @BindView(R.id.id_lecture_doc_bottom_layout)
    RelativeLayout mDocBottomLayout;
    @BindView(R.id.id_lecture_doc_progress)
    ProgressBar mDocProgress;
    @BindView(R.id.id_lecture_doc_display)
    DocView mDocView;
    @BindView(R.id.id_lecture_docppt_display)
    DocWebView mDocWebView;
    @BindView(R.id.id_lecture_doc_back)
    ImageButton mDocBack;
    @BindView(R.id.id_lecture_doc_forward)
    ImageButton mDocForward;
    @BindView(R.id.id_lecture_doc_img_grid)
    ImageButton mImgGrid;
    @BindView(R.id.id_lecture_doc_fullscreen)
    ImageButton mFullScreen;
    @BindView(R.id.id_lecture_prepare_layout)
    RelativeLayout mPrepareLayout;
    @BindView(R.id.id_lecture_pb)
    ProgressBar mPrepareBar;
    @BindView(R.id.id_lecture_doc_update_tip)
    TextView mUpdateTip;
    @BindView(R.id.id_lecture_doc_exit_fullscreen)
    ImageButton mExitFullScreen;
    @BindView(R.id.id_lecture_v_video_layout)
    RelativeLayout mVVideoLayout;
    @BindView(R.id.id_lecture_h_video_layout)
    RelativeLayout mHVideoLayout;
    RecyclerView mVideos;
    @BindView(R.id.id_lecture_video_full_screen_layout)
    RelativeLayout mVideoFullScreenLayout;
    CCSurfaceRenderer mFullScreenRenderer,mFullScreenRenderer1;
    @BindView(R.id.id_lecture_video_full_screen_container)
    RelativeLayout mSurfaceContainer;
    @BindView(R.id.id_lecture_video_full_screen_other_layout)
    RelativeLayout mOtherLayout;
    @BindView(R.id.id_lecture_video_full_screen_mic_close)
    ImageView mMicClose;
    @BindView(R.id.id_lecture_doc_rotate)
    RotateLayout mDocRotate;
    @BindView(R.id.id_lecture_video_layout)
    RelativeLayout mVideoLayout;
    @BindView(R.id.id_lecture_video_container)
    RelativeLayout mVideoSurfaceContainer;
    @BindView(R.id.id_lecture_drag_child)
    LinearLayout mDrawLayout;
    @BindView(R.id.id_lecture_draw_clear)
    ImageButton mClear;
    @BindView(R.id.id_lecture_page_change_layout)
    LinearLayout mPageChangeLayout;
    @BindView(R.id.id_lecture_bar_doc_back)
    ImageButton mBarDocBack;
    @BindView(R.id.id_lecture_bar_doc_forward)
    ImageButton mBarDocForward;
    @BindView(R.id.id_lecture_draw_paint)
    ImageButton mDrawPaint;
    @BindView(R.id.id_lecture_bar_doc_index)
    TextView mDocIndex;
    @BindView(R.id.id_lecture_draw_undo)
    ImageButton mUndo;

    private RotateLayout mPopupRoot;
    private View mPopupView;
    private ImageButton mSmallSize, mMidSize, mLargeSize;
    private RecyclerView mColors;
    private BubbleRelativeLayout mBubbleLayout;
    private BubblePopupWindow mPopupWindow;
    private final int[] mColorResIds = new int[]{
            R.drawable.black_selector, R.drawable.orange_selector, R.drawable.green_selector,
            R.drawable.blue_selector, R.drawable.gray_selector, R.drawable.red_selector
    };
    private final int[] mColorValues = new int[]{
            Color.parseColor("#000000"), Color.parseColor("#f27c19"), Color.parseColor("#27c127"),
            Color.parseColor("#4a9fda"), Color.parseColor("#8b8b8b"), Color.parseColor("#ce2626")
    };
    private final String[] mColorStr = new String[]{
            "000000", "f27a1a", "70c75e", "78a7f5", "7b797a", "e33423"
    };
    private int mDrawColorPosition = 0;

    private SubscribeRemoteStream mFullScreenStream,mFullScreenStream1;//集合是流的类。
    private boolean isClickVideoFullScreen = false;
    private int mCurPosition = -1;

    private String mLocalDocId;
    private int mLocalPosition;

    private boolean isAuth = false;
    private OnTeacherLectureListener mOnTeacherLectureListener;

    private RendererCommon.RendererEvents mRendererEvents = new RendererCommon.RendererEvents() {
        @Override
        public void onFirstFrameRendered() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mFullScreenRenderer != null) {
                        mFullScreenRenderer.setBackgroundColor(Color.TRANSPARENT); // 加载到第一帧数据设置背景透明
                    }
                }
            });
        }

        @Override
        public void onFrameResolutionChanged(int i, int i1, int i2) {

        }
    };
    private RendererCommon.RendererEvents mRendererEvents1 = new RendererCommon.RendererEvents() {
        @Override
        public void onFirstFrameRendered() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mFullScreenRenderer1 != null) {
                        mFullScreenRenderer1.setBackgroundColor(Color.TRANSPARENT); // 加载到第一帧数据设置背景透明
                    }
                }
            });
        }

        @Override
        public void onFrameResolutionChanged(int i, int i1, int i2) {

        }
    };

    public static LectureFragment newInstance(int role) {
        Bundle args = new Bundle();
        args.putInt(KEY_PARAM_ROLE, role);
        LectureFragment fragment = new LectureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initDrawPopup() {
        mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.draw_bubble_layout, null);
        mPopupRoot = (RotateLayout) mPopupView.findViewById(R.id.id_bubble_root);
//        mPopupRoot.setAngle(270);
        mBubbleLayout = (BubbleRelativeLayout) mPopupView.findViewById(R.id.id_bubble_layout);
        mPopupWindow = new BubblePopupWindow(mPopupView, mBubbleLayout);
        mPopupWindow.setCancelOnTouch(false);
        mPopupWindow.setCancelOnTouchOutside(true);
        mSmallSize = (ImageButton) mPopupView.findViewById(R.id.id_small_size);
        mMidSize = (ImageButton) mPopupView.findViewById(R.id.id_mid_size);
        mLargeSize = (ImageButton) mPopupView.findViewById(R.id.id_large_size);
        mColors = (RecyclerView) mPopupView.findViewById(R.id.id_draw_bubble_colors);
        mSmallSize.setOnClickListener(this);
        mMidSize.setOnClickListener(this);
        mLargeSize.setOnClickListener(this);
        mSmallSize.setSelected(true);
        mMidSize.setSelected(false);
        mLargeSize.setSelected(false);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        mColors.setLayoutManager(layoutManager);
        final ColorAdapter colorAdapter = new ColorAdapter(mActivity);
        ArrayList<ColorStatus> colorStatuses = new ArrayList<>();
        ColorStatus colorStatus;
        for (int i = 0; i < mColorResIds.length; i++) {
            colorStatus = new ColorStatus();
            if (i == 4) {
                colorStatus.setSelected(true);
            } else {
                colorStatus.setSelected(false);
            }
            colorStatus.setResId(mColorResIds[i]);
            colorStatuses.add(colorStatus);
        }
        colorAdapter.bindDatas(colorStatuses);
        mColors.addItemDecoration(new DividerGridItemDecoration(mActivity));
        mColors.setAdapter(colorAdapter);
        mColors.addOnItemTouchListener(new BaseOnItemTouch(mColors, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                final int position = mColors.getChildAdapterPosition(viewHolder.itemView);
                if (mDrawColorPosition == position) {
                    return;
                }
                ColorStatus pre = new ColorStatus();
                pre.setSelected(false);
                pre.setResId(colorAdapter.getDatas().get(mDrawColorPosition).getResId());
                colorAdapter.update(mDrawColorPosition, pre);
                ColorStatus cur = new ColorStatus();
                cur.setSelected(true);
                cur.setResId(colorAdapter.getDatas().get(position).getResId());
                colorAdapter.update(position, cur);
                mDrawColorPosition = position;
                // 设置颜色
                setColor(mColorValues[position], Integer.parseInt(mColorStr[position], 16));
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Config.DOC_GRID_RESULT_CODE) {
            int position = data.getIntExtra("doc_img_grid_position", 0);
            setDocImg(position);
        }
    }

    private void setDocImg(int position) {
        isFirstChangePage = false;
        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(position), position, mCurDocInfo.getDocId(), mCurDocInfo.getName());
        sendPageChange(position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        if (mCurDocInfo != null && mCurDocInfo.getDocId().equals(event.obj)) {
            mDocProgress.setVisibility(View.GONE);
            mImgGrid.setVisibility(View.GONE);
            mFullScreen.setVisibility(View.GONE);
            mCurDocInfo = null;
            sendCurrentDocPage();
        }
    }

    /**
     * 根据本地信息回复文档
     */
    public void restoreDoc() {
        if (mCurDocInfo == null) {
            mLocalDocId = mSPUtil.getString(Config.KEY_DOC_ID + Config.mRoomId);
            mLocalPosition = mSPUtil.getInt(KEY_DOC_POSITION);
            if (TextUtils.isEmpty(mLocalDocId) || mLocalPosition == -1) {
                // TODO: 2017/10/13  具体实现待商榷
                if (mInteractSession.isRoomLive()) {
                    sendPageChange(-1);
                }
                return;
            }
            fetchRoomDoc(0);
        } else {
            setDocInfo(mCurDocInfo, mCurPosition, 0);
        }
    }

    @Override
    public void onClick(View v) {
        clearSizesStatus();
        v.setSelected(!v.isSelected());
        float size = 1.5f;
        switch (v.getId()) {
            case R.id.id_small_size:
                size = 1.5f;
                break;
            case R.id.id_mid_size:
                size = 4.5f;
                break;
            case R.id.id_large_size:
                size = 7.5f;
                break;
        }
        // 设置尺寸
        setStrokeWidth(size);
    }

    private void clearSizesStatus() {
        mSmallSize.setSelected(false);
        mMidSize.setSelected(false);
        mLargeSize.setSelected(false);
    }

    private final class RetryTask implements Runnable {

        private int where;

        RetryTask(int where) {
            this.where = where;
        }

        @Override
        public void run() {
            fetchRoomDoc(where);
        }
    }

    private class RoomCallBack implements CCInteractSession.AtlasCallBack<DocInfo> {

        private int where;

        RoomCallBack(int where) {
            this.where = where;
        }

        @Override
        public void onSuccess(DocInfo docInfo) {
            if (mCurDocInfo != null) {
                return;
            }
            if (docInfo == null) {
                // 表示当前文档正在转换格式
                mHandler.postDelayed(new RetryTask(where), 1500);
                return;
            }
            setDocInfo(docInfo, mLocalPosition, where);
            if (where == 1 && isViewInitialize) {
                mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(mLocalPosition), mLocalPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                mPrepareLayout.setVisibility(View.GONE);
                mPrepareBar.setVisibility(View.GONE);
                mUpdateTip.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailure(String err) {
            // 做一次数据恢复
            mCurDocInfo = mPreDocInfo;
            mPreDocInfo = null;
            // 隐藏图片区
            if (where == 1 && isViewInitialize) {
                mPrepareLayout.setVisibility(View.GONE);
                mPrepareBar.setVisibility(View.GONE);
                mUpdateTip.setVisibility(View.GONE);
            }
            if (where == 1) {
                mPrepareLayout.setVisibility(View.GONE);
                mPrepareBar.setVisibility(View.GONE);
                mUpdateTip.setVisibility(View.GONE);
            }
        }
    }

    private void fetchRoomDoc(int where) {
        mInteractSession.getRoomDoc(null, mLocalDocId, new RoomCallBack(where));
    }

    public void sendCurrentDocPage() {
        if (mCurDocInfo == null) {
            mCurPosition = -1;
            mSPUtil.put(Config.KEY_DOC_ID + Config.mRoomId, "WhiteBorad");
            mSPUtil.put(KEY_DOC_POSITION, mCurPosition);
        }
        sendPageChange(mCurPosition);
    }

    /**
     * 老师授权学生为讲师，老师通知接口。
     * @param CurPosition
     */
    public void TeacherSetupTeacherPage(int CurPosition){
        mCurPosition = CurPosition;
        if (mOnTeacherLectureListener != null && mRole == CCInteractSession.PRESENTER) {
            mOnTeacherLectureListener.onLecture(CurPosition+1, mCurDocInfo.getPageTotalNum());
        }
        mDocIndex.setText(CurPosition+1 + "/" + mCurDocInfo.getPageTotalNum());
    }

    /**
     * 学生设为讲师，获取docinfo类。
     * @return
     */
    public DocInfo getDocInfo(){
        return mCurDocInfo;
    }
    /**
     *设为讲师的通知接口
     * @param docInfo
     * @param position
     */
    public void setupTeacherFlag(DocInfo docInfo, int position){
        mCurDocInfo = docInfo;
        mCurPosition = position;
        if(mCurDocInfo.getStep() != -1){
            stepnum = docInfo.getStep();
        }
        if(mCurDocInfo.getPageTotalNum() == 0){
            mImgGrid.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mPageChangeLayout.setVisibility(View.GONE);
        } else {
            if (!isDocFullScreen) {
                if (CCApplication.sClassDirection == 0) {
                    mImgGrid.setVisibility(View.VISIBLE);
                } else {
                    mImgGrid.setVisibility(View.GONE);
                }
            } else {
                mPageChangeLayout.setVisibility(View.VISIBLE);
            }

        }
        isFirstChangePage = true;
//        if (mOnTeacherLectureListener != null&&mRole == CCInteractSession.TALKER) {
//            mOnTeacherLectureListener.onLecture(mCurPosition+1, docInfo.getAllImgUrls().size());
//        }
        if(mCurDocInfo.isSetupTeacher()&&mRole == CCInteractSession.TALKER){
            setupTeacherPageChange(position);
        }
    }
    private void setupTeacherPageChange(int position){
        // 设为讲师，通知学生更新界面，横屏界面。
        if (mOnTeacherLectureListener != null) {
            mOnTeacherLectureListener.onLecture(position+1, mCurDocInfo.getPageTotalNum());
        }
        mDocIndex.setText(position+1 + "/" + mCurDocInfo.getPageTotalNum());

        if (mCurDocInfo.getPageTotalNum() == 1) {
            if(isDocFullScreen){
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            } else {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            }
            mBarDocBack.setEnabled(false);
            mBarDocForward.setEnabled(false);
        } else {
            if (position == 0) {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen){
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                    mDocBack.setEnabled(true);
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(true);
                }
            } else if (position == mCurDocInfo.getPageTotalNum() - 1) {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen){
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        if(mCurDocInfo.isUseSDK()){
                            mDocForward.setVisibility(View.VISIBLE);
                            mDocBack.setVisibility(View.VISIBLE);
                            mDocWebView.setVisibility(View.VISIBLE);
                        } else {
                            if(mCurDocInfo.getPageTotalNum() == 0){
                                mDocForward.setVisibility(View.GONE);
                                mDocBack.setVisibility(View.GONE);
                                mDocWebView.setVisibility(View.GONE);
                            } else {
                                mDocForward.setVisibility(View.VISIBLE);
                                mDocBack.setVisibility(View.VISIBLE);
                                mDocWebView.setVisibility(View.GONE);
                            }
                        }
                    }
                    mDocBack.setEnabled(true);
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(true);
                }
            } else {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen) {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                    mDocBack.setEnabled(true);
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(true);
                }
            }
        }
//        if(position == -1){
//            mDocForward.setVisibility(View.GONE);
//            mDocBack.setVisibility(View.GONE);
//        }
        if(CCApplication.sClassDirection == 1){
            mDocForward.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
        }
    }

    /**
     * 处理画笔错误问题，监听当前文档是白板还是ppt动画（目前这种方式处理，等引入极速文档不需要这样去处理）
     * @param position
     * @param issdk
     */
    public void setPageChange(int position,boolean issdk){
        mPageIndex = position;
        isSDK = issdk;
    }

    public void setDocInfo(DocInfo docInfo, int position, int where) {
        mCurDocInfo = docInfo;
        mCurPosition = position;
        stepnum = docInfo.getStep();
        if (!isViewInitialize) {// 布局以及销毁
            return;
        }
        
        mDocProgress.setVisibility(View.GONE);
        mDocProgress.setMax(docInfo.getPageTotalNum());
        mDocProgress.setProgress(mCurPosition + 1);
        if(CCApplication.sClassDirection == 1){
            mFullScreen.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
        } else {
            mFullScreen.setVisibility(View.VISIBLE);
        }
        if (where == 0) {
            if (docInfo.getAllImgUrls().size() > 1) {
                if(mCurDocInfo.isSetupTeacher()&&mRole == CCInteractSession.TALKER){
                    if(CCApplication.sClassDirection == 1){
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        if(isDocFullScreen) {
                            mImgGrid.setVisibility(View.GONE);
                        } else {
                            mImgGrid.setVisibility(View.VISIBLE);
                        }
                        mDocForward.setVisibility(View.VISIBLE);
                        mDocBack.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mRole == CCInteractSession.PRESENTER) {
                        if(CCApplication.sClassDirection == 1){
                            mImgGrid.setVisibility(View.GONE);
                            mDocForward.setVisibility(View.GONE);
                        } else {
                            mImgGrid.setVisibility(View.VISIBLE);
                            if(isDocFullScreen) {
                                mDocForward.setVisibility(View.GONE);
                            } else {
                                mDocForward.setVisibility(View.VISIBLE);
                            }
                        }
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        mImgGrid.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    }
                }
            } else {
                mImgGrid.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
                mDocBack.setVisibility(View.GONE);
            }
            mPrepareLayout.setVisibility(View.GONE);
        } else {
            mPrepareLayout.setVisibility(View.VISIBLE);
            mImgGrid.setVisibility(View.GONE);
        }
        // 通知老师更新界面
        if (mOnTeacherLectureListener != null) {
            mOnTeacherLectureListener.onLecture(mCurPosition+1, docInfo.getAllImgUrls().size());
        }

        mSPUtil.put(Config.KEY_DOC_ID + Config.mRoomId, mCurDocInfo.getDocId());
        mSPUtil.put(KEY_DOC_POSITION, mCurPosition);
        if (mInteractSession.isRoomLive() ) {
            if(mCurDocInfo.isSetupTeacher()&&mRole == CCInteractSession.TALKER){
                isFirstChangePage = true;
                sendPageChange(mCurPosition);
            } else {
                if(mRole == CCInteractSession.PRESENTER) {
                    isFirstChangePage = false;
                    sendPageChange(mCurPosition);
                }
            }
        }
    }
    private void sendPageChange2(int position){
//        if(!isFirstChangePage){
        if(position <= mCurDocInfo.getPageTotalNum() - 1){
            String url = mCurDocInfo.getAllImgUrls().get(position);
//            mInteractSession.docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
//                    url, mCurDocInfo.isUseSDK(),mCurDocInfo.getDocMode(),position, DensityUtil
//                            .getWidth(mActivity),DensityUtil.getWidth(mActivity) * 9 / 16);
            mInteractSession.docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
                    url, mCurDocInfo.isUseSDK(),position);
        }
//        }
    }

    //发送文档翻页事件给对方
    private void sendPageChange(int position) {
        if (position == -1) { // 发送白板
            if(mRole == CCInteractSession.TALKER){
                return;
            }
//            mInteractSession.docPageChange("WhiteBorad", "WhiteBorad", position, "#", false,
//                    0, position, DensityUtil.getWidth(mActivity),DensityUtil.getWidth(mActivity) * 9 / 16);
            mInteractSession.docPageChange("WhiteBorad", "WhiteBorad", position, "#", false,
                     position);
            return;
        }
        // 通知老师更新界面
        if (mOnTeacherLectureListener != null) {
            mOnTeacherLectureListener.onLecture(position+1, mCurDocInfo.getPageTotalNum());
        }
        mDocIndex.setText(position+1 + "/" + mCurDocInfo.getPageTotalNum());

        if (mCurDocInfo.getPageTotalNum() == 1) {
            if(isDocFullScreen){
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            } else {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            }
            mBarDocBack.setEnabled(false);
            mBarDocForward.setEnabled(false);
        } else {
            if (position == 0) {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen){
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                    mDocBack.setEnabled(true);
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(true);
                }
            } else if (position == mCurDocInfo.getPageTotalNum() - 1) {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen){
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        if(mCurDocInfo.isUseSDK()){
                            mDocForward.setVisibility(View.VISIBLE);
                            mDocBack.setVisibility(View.VISIBLE);
                            mDocWebView.setVisibility(View.VISIBLE);
                        } else {
                            mDocForward.setVisibility(View.VISIBLE);
                            mDocBack.setVisibility(View.VISIBLE);
                            mDocWebView.setVisibility(View.GONE);
                        }
                    }
                    mDocBack.setEnabled(true);
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(true);
                }
            } else {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen) {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                    mDocBack.setEnabled(true);
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(true);
                }
            }
        }
        if(!isFirstChangePage){
            String url = mCurDocInfo.getAllImgUrls().get(position);
//            mInteractSession.docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
//                    url, mCurDocInfo.isUseSDK(),mCurDocInfo.getDocMode(),position, DensityUtil
//                            .getWidth(mActivity),DensityUtil.getWidth(mActivity) * 9 / 16);
            mInteractSession.docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
                    url, mCurDocInfo.isUseSDK(),position);
        }
    }

}
