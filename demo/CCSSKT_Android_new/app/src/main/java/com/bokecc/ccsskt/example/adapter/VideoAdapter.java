package com.bokecc.ccsskt.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.VideoStreamView;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.base.renderer.CCSurfaceRenderer;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class VideoAdapter extends BaseRecycleAdapter<VideoAdapter.LittleVideoViewHolder, VideoStreamView> {

    private ConcurrentHashMap<CCSurfaceRenderer, RelativeLayout> views = new ConcurrentHashMap<>();
    private ConcurrentHashMap<RelativeLayout, CCSurfaceRenderer> mRootRenderers = new ConcurrentHashMap<>();

    private int mType;
    private boolean isRefresh = false;

    public VideoAdapter(Context context) {
        super(context);
    }

    public void setType(int type) {
        mType = type;
    }

    public void refresh() {
        isRefresh = true;
    }

    @Override
    public void onBindViewHolder(VideoAdapter.LittleVideoViewHolder holder, int position) {
        VideoStreamView videoStreamView = mDatas.get(position);
        CCSurfaceRenderer renderer = videoStreamView.getRenderer();
        RelativeLayout.LayoutParams params;
        if (mType == CCInteractSession.TEMPLATE_TILE) {
            if (isRefresh) {
                calItemHeight(holder.itemView);
                isRefresh = false;
            }
            if (mDatas.size() == 1) {
                holder.mLittleUsername.setVisibility(View.INVISIBLE);
            } else {
                holder.mLittleUsername.setVisibility(View.VISIBLE);
            }
            if (CCApplication.sClassDirection == 0) {
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
        } else {
            holder.mLittleUsername.setVisibility(View.VISIBLE);
            if (CCApplication.sClassDirection == 0) {
                params = new RelativeLayout.LayoutParams(DensityUtil.dp2px(mContext, 80),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        DensityUtil.dp2px(mContext, 80));
            }
        }
        if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
            holder.mLittleVideoOne.setVisibility(View.VISIBLE);
            holder.mLittleVideoOper.setVisibility(View.GONE);
        } else {
            holder.mLittleVideoOne.setVisibility(View.GONE);
            holder.mLittleVideoOper.setVisibility(View.VISIBLE);
        }
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        renderer.setLayoutParams(params);
        if (mType == CCInteractSession.TEMPLATE_SINGLE ||
                mType == CCInteractSession.TEMPLATE_DOUBLE_TEACHER) {
            renderer.setZOrderOnTop(true);
            renderer.setZOrderMediaOverlay(true);
            renderer.bringToFront();
        } else {
            renderer.setZOrderOnTop(false);
            renderer.setZOrderMediaOverlay(false);
        }
        if (views.get(renderer) != null) { // 判断当前需要被添加的子布局是否有父布局
            views.get(renderer).removeView(renderer); // 找到该子布局的父布局，从父布局移除
        }
        if (mRootRenderers.get(holder.mLittleItemRoot) != null) { // 如果跟布局下面有渲染布局 移除该渲染布局
            holder.mLittleItemRoot.removeView(mRootRenderers.get(holder.mLittleItemRoot));
        }
        holder.mLittleItemRoot.addView(renderer, -1);
        views.put(renderer, holder.mLittleItemRoot);
        mRootRenderers.put(holder.mLittleItemRoot, renderer); // 存放根布局和渲染布局
        if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
            holder.mLittleOneUsername.setText(videoStreamView.getStream().getUserName());
        } else {
            holder.mLittleUsername.setText(videoStreamView.getStream().getUserName());
        }
        holder.mMicIcon.setVisibility(View.VISIBLE);
        if (videoStreamView.getStream().isAllowAudio()) {
            if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                Glide.with(mContext).load(R.drawable.mic_open_icon).into(holder.mOneMicIcon);
            } else {
                Glide.with(mContext).load(R.drawable.mic_open_icon).into(holder.mMicIcon);
            }
        } else {
            if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                Glide.with(mContext).load(R.drawable.mic_close_icon).into(holder.mOneMicIcon);
            } else {
                Glide.with(mContext).load(R.drawable.mic_close_icon).into(holder.mMicIcon);
            }
        }
        if (videoStreamView.getStream().isAllowDraw()) {
            if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                holder.mOneDrawIcon.setVisibility(View.VISIBLE);
            } else {
                holder.mDrawIcon.setVisibility(View.VISIBLE);
            }
        } else {
            if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                holder.mOneDrawIcon.setVisibility(View.GONE);
            } else {
                holder.mDrawIcon.setVisibility(View.GONE);
            }
        }
        if (videoStreamView.getStream().isSetupTeacher()) {
            if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                holder.mOneSetupTheacher.setVisibility(View.VISIBLE);
            } else {
                holder.mSetupTheacherIcon.setVisibility(View.VISIBLE);
            }
        } else {
            if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                holder.mOneSetupTheacher.setVisibility(View.GONE);
            } else {
                holder.mSetupTheacherIcon.setVisibility(View.GONE);
            }
        }
        if (CCInteractSession.getInstance().getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
            if (videoStreamView.getStream().isLock()) {
                if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneLockIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.mLockIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneLockIcon.setVisibility(View.GONE);
                } else {
                    holder.mLockIcon.setVisibility(View.GONE);
                }
            }
        } else {
            holder.mLockIcon.setVisibility(View.GONE);
            holder.mOneLockIcon.setVisibility(View.GONE);
        }
        // 学生仅音频模式
        if (videoStreamView.getStream().getUserRole() != CCInteractSession.PRESENTER &&
                CCInteractSession.getInstance().getMediaMode() == CCInteractSession.MEDIA_MODE_AUDIO) {
            if (videoStreamView.getStream().getUserId().equals(CCInteractSession.SHARE_SCREEN_STREAM_ID)) {
                holder.mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
            } else {
                holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                if (CCApplication.sClassDirection == 0) {
                    Glide.with(mContext).load(R.drawable.only_mic_bg).asBitmap().into(holder.mOtherIcon);
                } else {
                    Glide.with(mContext).load(R.drawable.only_mic_bg_land).asBitmap().into(holder.mOtherIcon);
                }
            }
        } else {
            if (!videoStreamView.getStream().isAllowVideo()) { // 关闭视频
                holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                if (CCApplication.sClassDirection == 0) {
                    Glide.with(mContext).load(R.drawable.camera_close_bg).asBitmap().into(holder.mOtherIcon);
                } else {
                    Glide.with(mContext).load(R.drawable.camera_close_bg_land).asBitmap().into(holder.mOtherIcon);
                }
            } else {
                holder.mOtherLayout.setVisibility(View.GONE);
                if (videoStreamView.getStream().getRemoteStream() != null) {
                    if (!videoStreamView.getStream().getRemoteStream().hasAudio()) {
                        holder.mMicIcon.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(R.drawable.no_mic_icon).into(holder.mMicIcon);
                    }
                    if (!videoStreamView.getStream().getRemoteStream().hasVideo()) {
                        holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                        if (CCApplication.sClassDirection == 0) {
                            Glide.with(mContext).load(R.drawable.no_camera_icon).asBitmap().into(holder.mOtherIcon);
                        } else {
                            Glide.with(mContext).load(R.drawable.no_camera_icon_land).asBitmap().into(holder.mOtherIcon);
                        }
                    }
                }
            }
        }
        // 共享桌面流不显示麦克风图标
        if (videoStreamView.getStream().getUserName().equals(CCInteractSession.SHARE_SCREEN_STREAM_NAME)) {
            holder.mMicIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(LittleVideoViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                holder.mLittleVideoOne.setVisibility(View.VISIBLE);
                holder.mLittleVideoOper.setVisibility(View.GONE);
            } else {
                holder.mLittleVideoOne.setVisibility(View.GONE);
                holder.mLittleVideoOper.setVisibility(View.VISIBLE);
            }
            VideoStreamView videoStreamView = mDatas.get(position);
            if ((int) payloads.get(0) == 0) {
                if (videoStreamView.getStream().isAllowAudio()) {
                    if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                        Glide.with(mContext).load(R.drawable.mic_open_icon).into(holder.mOneMicIcon);
                    } else {
                        Glide.with(mContext).load(R.drawable.mic_open_icon).into(holder.mMicIcon);
                    }
                } else {
                    if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                        Glide.with(mContext).load(R.drawable.mic_close_icon).into(holder.mOneMicIcon);
                    } else {
                        Glide.with(mContext).load(R.drawable.mic_close_icon).into(holder.mMicIcon);
                    }
                }
            } else if ((int) payloads.get(0) == 1) {
                // 学生仅音频模式
                if (videoStreamView.getStream().getUserRole() != CCInteractSession.PRESENTER &&
                        CCInteractSession.getInstance().getMediaMode() == CCInteractSession.MEDIA_MODE_AUDIO) {
                    if (videoStreamView.getStream().getUserId().equals(CCInteractSession.SHARE_SCREEN_STREAM_ID)) {
                        holder.mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
                    } else {
                        holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                        if (CCApplication.sClassDirection == 0) {
                            Glide.with(mContext).load(R.drawable.only_mic_bg).asBitmap().into(holder.mOtherIcon);
                        } else {
                            Glide.with(mContext).load(R.drawable.only_mic_bg_land).asBitmap().into(holder.mOtherIcon);
                        }
                    }
                } else {
                    if (!videoStreamView.getStream().isAllowVideo()) { // 关闭视频
                        holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                        if (CCApplication.sClassDirection == 0) {
                            Glide.with(mContext).load(R.drawable.camera_close_bg).asBitmap().into(holder.mOtherIcon);
                        } else {
                            Glide.with(mContext).load(R.drawable.camera_close_bg_land).asBitmap().into(holder.mOtherIcon);
                        }
                    } else {
                        holder.mOtherLayout.setVisibility(View.GONE);
                        if (videoStreamView.getStream().getRemoteStream() != null) {
                            if (!videoStreamView.getStream().getRemoteStream().hasAudio()) {
                                holder.mMicIcon.setVisibility(View.VISIBLE);
                                Glide.with(mContext).load(R.drawable.no_mic_icon).into(holder.mMicIcon);
                            }
                            if (!videoStreamView.getStream().getRemoteStream().hasVideo()) {
                                holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                                if (CCApplication.sClassDirection == 0) {
                                    Glide.with(mContext).load(R.drawable.no_camera_icon).asBitmap().into(holder.mOtherIcon);
                                } else {
                                    Glide.with(mContext).load(R.drawable.no_camera_icon_land).asBitmap().into(holder.mOtherIcon);
                                }
                            }
                        }
                    }
                }
                // 共享桌面流不显示麦克风图标
                if (videoStreamView.getStream().getUserName().equals(CCInteractSession.SHARE_SCREEN_STREAM_NAME)) {
                    holder.mMicIcon.setVisibility(View.GONE);
                }
            } else if ((int) payloads.get(0) == 2) {
                if (videoStreamView.getStream().isAllowDraw()) {
                    if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                        holder.mOneDrawIcon.setVisibility(View.VISIBLE);
                    } else {
                        holder.mDrawIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                        holder.mOneDrawIcon.setVisibility(View.GONE);
                    } else {
                        holder.mDrawIcon.setVisibility(View.GONE);
                    }
                }
            } else if ((int) payloads.get(0) == 3) {
                if (CCInteractSession.getInstance().getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
                    if (videoStreamView.getStream().isLock()) {
                        if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                            holder.mOneLockIcon.setVisibility(View.VISIBLE);
                        } else {
                            holder.mLockIcon.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                            holder.mOneLockIcon.setVisibility(View.GONE);
                        } else {
                            holder.mLockIcon.setVisibility(View.GONE);
                        }
                    }
                } else {
                    holder.mLockIcon.setVisibility(View.GONE);
                    holder.mOneLockIcon.setVisibility(View.GONE);
                }
            } else if ((int) payloads.get(0) == 4) {
                if (videoStreamView.getStream().isSetupTeacher()) {
                    if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                        holder.mOneSetupTheacher.setVisibility(View.VISIBLE);
                    } else {
                        holder.mSetupTheacherIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mType == CCInteractSession.TEMPLATE_TILE && mDatas.size() == 1) {
                        holder.mOneSetupTheacher.setVisibility(View.GONE);
                    } else {
                        holder.mSetupTheacherIcon.setVisibility(View.GONE);
                    }
                }
            } else {
                onBindViewHolder(holder, position);
            }
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.little_item_video_layout;
    }

    @Override
    public VideoAdapter.LittleVideoViewHolder getViewHolder(View itemView, int viewType) {
        if (mType == CCInteractSession.TEMPLATE_TILE) {
            calItemHeight(itemView);
        } else {
            if (CCApplication.sClassDirection == 1) { // 横屏
                itemView.getLayoutParams().width = DensityUtil.dp2px(mContext, 143.3f);
                itemView.getLayoutParams().height = DensityUtil.dp2px(mContext, 81);
            }
        }
        return new LittleVideoViewHolder(itemView);
    }

    private void calItemHeight(View itemView) {
        Rect outRect = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int height = outRect.bottom - outRect.top;
        int width = DensityUtil.getWidth(mContext);
        int size = mDatas.size();
        int spanCount = (int) Math.ceil(Math.sqrt(size));
        itemView.getLayoutParams().height = height / spanCount;
        itemView.getLayoutParams().width = width / spanCount;
    }

    final class LittleVideoViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_little_video_item_root)
        RelativeLayout mLittleItemRoot;
        @BindView(R.id.id_little_video_item_username)
        TextView mLittleUsername;
        @BindView(R.id.id_little_video_item_mic)
        ImageView mMicIcon;
        @BindView(R.id.id_little_video_item_draw)
        ImageView mDrawIcon;
        @BindView(R.id.id_little_video_item_setup_theacher)
        ImageView mSetupTheacherIcon;
        @BindView(R.id.id_little_video_item_lock)
        ImageView mLockIcon;
        @BindView(R.id.id_little_video_item_other_layout)
        RelativeLayout mOtherLayout;
        @BindView(R.id.id_little_video_item_other_icon)
        ImageView mOtherIcon;
        @BindView(R.id.id_little_video_oper)
        RelativeLayout mLittleVideoOper;
        @BindView(R.id.id_little_video_one_item)
        LinearLayout mLittleVideoOne;
        @BindView(R.id.id_little_video_one_item_name)
        TextView mLittleOneUsername;
        @BindView(R.id.id_little_video_one_item_mic_close)
        ImageView mOneMicIcon;
        @BindView(R.id.id_little_video_one_item_video_draw)
        ImageView mOneDrawIcon;
        @BindView(R.id.id_little_video_one_item_video_setup_theacher)
        ImageView mOneSetupTheacher;
        @BindView(R.id.id_little_video_one_item_video_lock)
        ImageView mOneLockIcon;


        LittleVideoViewHolder(View itemView) {
            super(itemView);
        }
    }

}
