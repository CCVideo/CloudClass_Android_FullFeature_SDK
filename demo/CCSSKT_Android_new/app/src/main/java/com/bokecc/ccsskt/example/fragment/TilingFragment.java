package com.bokecc.ccsskt.example.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.VideoStreamView;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.MyGridLayoutManager;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.sskt.CCInteractSession;

import butterknife.BindView;
import butterknife.OnClick;

public class TilingFragment extends BaseFragment {

    private static final String TAG = TilingFragment.class.getSimpleName();

    private MyGridLayoutManager mGridLayoutManager;
    private int mCurSpanCount = -1;

    public TilingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRole == CCInteractSession.PRESENTER) {
            mTeacherInteractionListener.dismissFollow();
            mTeacherInteractionListener.dismissVideoController();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_tiling;
    }

    @Override
    protected void setUpView() {
        int size = mVideoAdapter.getDatas().size();
        if (size > 0) {
            mCurSpanCount = (int) Math.ceil(Math.sqrt(size));
            mGridLayoutManager = new MyGridLayoutManager(mActivity, mCurSpanCount);
            mVideos.setLayoutManager(mGridLayoutManager);
        }
        mVideos.setAdapter(mVideoAdapter);
        if (mRole == CCInteractSession.PRESENTER) {
            mReceiveClick.setVisibility(View.GONE);
            mVideos.addOnItemTouchListener(new BaseOnItemTouch(mVideos, new OnClickListener() {
                @Override
                public void onClick(RecyclerView.ViewHolder viewHolder) {
                    int position = mVideos.getChildAdapterPosition(viewHolder.itemView);
                    if (mVideoStreamViews.get(position).getStream().getUserRole() == CCInteractSession.PRESENTER) {
                        return;
                    }
                    VideoStreamView videoStreamView = mVideoStreamViews.get(position);
                    if (mVideoClickListener != null) {
                        mVideoClickListener.onVideoClick(position, videoStreamView);
                    }
                }
            }));
        } else {
            mReceiveClick.setVisibility(View.VISIBLE);
            mReceiveClick.setEnabled(true);
            mReceiveClick.setClickable(true);
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mVideos;
    }

    @Override
    public void classStop() {
        super.classStop();
        mCurSpanCount = -1;
    }

    @Override
    public synchronized void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd) {
        if (mVideoStreamViews == null) {
            return;
        }
        if (isAdd) {
            for (VideoStreamView temp :
                    mVideoStreamViews) {
                if (temp.getStream().getUserId().equals(videoStreamView.getStream().getUserId())) {
                    return;
                }
            }
            mVideoStreamViews.add(position, videoStreamView);
        } else {
            mVideoStreamViews.remove(videoStreamView);
        }
        refreshLayout(mVideoStreamViews.size(), position, isAdd);
    }

    @Override
    public void notifyHandUp() {
        mReceiveClick.setEnabled(false);
        mDisplayInteractionListener.toggleTopAndBottom();
    }

    @Override
    public void notifyLayoutManagerRefresh() {
        mGridLayoutManager.setRefresh(true);
    }

    private void refreshLayout(int size, int position, boolean isAdd) {
        if (size == 0 || !isViewInitialize) {
            return;
        }
        int spanCount = (int) Math.ceil(Math.sqrt(size));
        if (mCurSpanCount != spanCount) {
            mCurSpanCount = spanCount;
            if (mGridLayoutManager != null) {
                mVideos.getRecycledViewPool().clear();
                mGridLayoutManager.removeAllViews();
                mGridLayoutManager.setSpanCount(mCurSpanCount);
                mGridLayoutManager.requestLayout();
                mVideoAdapter.refresh();
                mVideoAdapter.notifyDataSetChanged();
            } else {
                mGridLayoutManager = new MyGridLayoutManager(mActivity, mCurSpanCount);
                mVideos.setLayoutManager(mGridLayoutManager);
                mVideoAdapter.notifyDataSetChanged();
            }
        } else {
            mGridLayoutManager.setRefresh(true);
            if (isAdd) {
                mVideoAdapter.notifyItemInserted(position);
            } else {
                mVideoAdapter.notifyItemRemoved(position);
            }
            if (position != mVideoStreamViews.size()) {
                mVideoAdapter.notifyItemRangeChanged(position, mVideoStreamViews.size() - position);
            }
        }
    }

    public void restoreClick() {
        if (mReceiveClick != null) {
            mReceiveClick.setEnabled(true);
        }
    }

    @OnClick(R.id.id_tiling_receive_click)
    void animateTopAndBottom() {
        // TODO: 2017/10/27
        mReceiveClick.setEnabled(false);
        mDisplayInteractionListener.toggleTopAndBottom();
    }

    @BindView(R.id.id_tiling_videos)
    RecyclerView mVideos;
    @BindView(R.id.id_tiling_receive_click)
    FrameLayout mReceiveClick;

    public static TilingFragment newInstance(int role) {
        Bundle args = new Bundle();
        args.putInt(KEY_PARAM_ROLE, role);
        TilingFragment fragment = new TilingFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
