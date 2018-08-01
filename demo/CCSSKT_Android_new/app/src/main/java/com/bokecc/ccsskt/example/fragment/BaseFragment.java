package com.bokecc.ccsskt.example.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bokecc.ccsskt.example.adapter.VideoAdapter;
import com.bokecc.ccsskt.example.bridge.OnDisplayInteractionListener;
import com.bokecc.ccsskt.example.bridge.OnTeacherInteractionListener;
import com.bokecc.ccsskt.example.bridge.OnVideoClickListener;
import com.bokecc.ccsskt.example.bridge.OnVideoInteractionListener;
import com.bokecc.ccsskt.example.entity.VideoStreamView;
import com.bokecc.ccsskt.example.popup.LoadingPopup;
import com.bokecc.ccsskt.example.recycle.MyItemAnimator;
import com.bokecc.ccsskt.example.util.SPUtil;
import com.bokecc.sskt.CCInteractSession;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected Activity mActivity;
    protected View mRoot;
    protected boolean isViewInitialize = false;

    protected EventBus mEventBus;
    protected Handler mHandler;

    protected SPUtil mSPUtil;
    private Unbinder mUnbinder;
    protected CCInteractSession mInteractSession;
    protected VideoAdapter mVideoAdapter;
    protected CopyOnWriteArrayList<VideoStreamView> mVideoStreamViews;
    protected View mContentView;
    protected int mRole;
    protected OnTeacherInteractionListener mTeacherInteractionListener;
    protected OnVideoInteractionListener mVideoInteractionListener;
    protected OnDisplayInteractionListener mDisplayInteractionListener;
    protected OnVideoClickListener mVideoClickListener;
    private LoadingPopup mLoadingPopup;

    protected static final String KEY_PARAM_ROLE = "role";

    protected BaseFragment() {
        // Required empty public constructor
        mInteractSession = CCInteractSession.getInstance();
        mEventBus = EventBus.getDefault();
        mSPUtil = SPUtil.getIntsance();
        mHandler = new Handler(Looper.getMainLooper());
        mVideoStreamViews = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().keySet().contains(KEY_PARAM_ROLE)) {
            throw new NullPointerException();
        }
        mRole = getArguments().getInt(KEY_PARAM_ROLE);
        initLoadingPopup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(getContentViewId(), container, false);
        mUnbinder = ButterKnife.bind(this, mContentView);
        mRoot = mContentView;
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewInitialize = true;
        transformData();
        mVideoAdapter.bindDatas(mVideoStreamViews);
        setUpView();
        getRecyclerView().setItemAnimator(new MyItemAnimator());
        ((SimpleItemAnimator)getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
    }

    protected void transformData() {
        // Ignore 子类需要的时候进行实现
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        clearDatas();
        isViewInitialize = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        if (context instanceof OnTeacherInteractionListener) {
            mTeacherInteractionListener = (OnTeacherInteractionListener) context;
        }
        if (context instanceof OnVideoInteractionListener) {
            mVideoInteractionListener = (OnVideoInteractionListener) context;
        }
        if (context instanceof OnDisplayInteractionListener) {
            mDisplayInteractionListener = (OnDisplayInteractionListener) context;
        }
        if (context instanceof OnVideoClickListener) {
            mVideoClickListener = (OnVideoClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    protected View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    private void initLoadingPopup() {
        mLoadingPopup = new LoadingPopup(mActivity);
        mLoadingPopup.setOutsideCancel(false);
        mLoadingPopup.setKeyBackCancel(false);
    }

    protected void showLoading() {
        mLoadingPopup.show(mRoot);
    }

    protected void dismissLoading() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mLoadingPopup.dismiss();
            }
        });
    }

    protected void go(Class clazz) {
        Intent intent = new Intent(mActivity, clazz);
        startActivity(intent);
    }

    protected void go(Class clazz, Bundle bundle) {
        Intent intent = new Intent(mActivity, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void go(Class clazz, int requestCode) {
        Intent intent = new Intent(mActivity, clazz);
        startActivityForResult(intent, requestCode);
    }

    protected void go(Class clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(mActivity, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    protected void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastOnUiThread(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(msg);
            }
        });
    }

    protected abstract int getContentViewId();

    protected abstract void setUpView();

    public abstract RecyclerView getRecyclerView();

    public abstract void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd);

    public abstract void notifyHandUp();

    public void setVideoAdapter(VideoAdapter videoAdapter) {
        mVideoAdapter = videoAdapter;
    }

    public void clearDatas() {
        if (mVideoStreamViews != null) {
            mVideoStreamViews.clear();
            mVideoAdapter.clear();
        }
    }

    public void addDatas(CopyOnWriteArrayList<VideoStreamView> datas) {
        mVideoStreamViews.addAll(datas);
    }

    public void notifySelfRemove(VideoStreamView selfView) {
        mVideoStreamViews.remove(selfView);
        mVideoAdapter.notifyDataSetChanged();
    }

    public void restoreClick() {
    }

    public void classStop() {
    }

    public void notifyLayoutManagerRefresh() {}
}
