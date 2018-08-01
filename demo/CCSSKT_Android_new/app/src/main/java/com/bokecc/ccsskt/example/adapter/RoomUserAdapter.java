package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.entity.RoomUser;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.User;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class RoomUserAdapter extends BaseRecycleAdapter<RoomUserAdapter.RoomUserViewHolder, RoomUser> {

    private int mLianmaiMode;
    private int mType;

    public RoomUserAdapter(Context context, int type, @CCInteractSession.LianmaiMode int lianmaiMode) {
        super(context);
        mType = type;
        mLianmaiMode = lianmaiMode;
    }

    @Override
    public void onBindViewHolder(RoomUserViewHolder holder, final int position) {
        RoomUser roomUser = mDatas.get(position);
        User user = roomUser.getUser();
        if (mType == CCInteractSession.PRESENTER && user.getUserRole() != CCInteractSession.PRESENTER) {
            holder.mArrow.setVisibility(View.VISIBLE);
        } else {
            if (mType == CCInteractSession.TALKER) {
                holder.mArrow.setVisibility(View.GONE);
            } else {
                holder.mArrow.setVisibility(View.INVISIBLE);
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                    holder.mLianmai.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.rightMargin = DensityUtil.dp2px(mContext, 10);
            holder.mLianmai.setLayoutParams(params);
        }
        if (user.getPlatForm() == CCInteractSession.MOBILE) {
            holder.mDevice.setImageResource(R.drawable.user_phone);
        } else {
            holder.mDevice.setImageResource(R.drawable.user_computer);
        }
        if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_MAI_ING) {
            holder.mLianmai.setVisibility(View.VISIBLE);
            holder.mStatusWait.setVisibility(View.GONE);
            holder.mStatusing.setVisibility(View.VISIBLE);
        } else if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IN_MAI) {
            holder.mLianmai.setVisibility(View.VISIBLE);
            holder.mStatusing.setVisibility(View.GONE);
            holder.mStatusWait.setVisibility(View.VISIBLE);
            int resId;
            String value;
            Drawable drawable;
            if (mLianmaiMode == CCInteractSession.LIANMAI_MODE_FREE) {
                resId = R.drawable.user_wait_icon;
                value = "第" + roomUser.getMaiIndex() + "位,排麦中...";
            } else {
                resId = R.drawable.user_hand_icon;
                value = "第" + roomUser.getMaiIndex() + "位,举手中...";
            }
            drawable = mContext.getResources().getDrawable(resId);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mStatusWait.setCompoundDrawables(drawable, null, null, null);
            holder.mStatusWait.setText(value);
        } else if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IDLE) {
            holder.mLianmai.setVisibility(View.GONE);
            holder.mStatusWait.setVisibility(View.GONE);
            holder.mStatusing.setVisibility(View.GONE);
        } else if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_INVITE_MAI) {
            holder.mLianmai.setVisibility(View.VISIBLE);
            holder.mStatusing.setVisibility(View.GONE);
            holder.mStatusWait.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.user_invite_icon);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mStatusWait.setCompoundDrawablePadding(DensityUtil.dp2px(mContext, 3));
            holder.mStatusWait.setCompoundDrawables(drawable, null, null, null);
            holder.mStatusWait.setText("邀请连麦中...");
        }
        holder.mUserName.setText(user.getUserName());
        if (user.getUserRole() == CCInteractSession.PRESENTER) {
            holder.mIdentity.setVisibility(View.VISIBLE);
            holder.mGag.setVisibility(View.GONE);
            holder.mLianmai.setVisibility(View.VISIBLE);
        } else {
            holder.mIdentity.setVisibility(View.GONE);
            if (user.getUserSetting().isHandUp()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        holder.mHand.getLayoutParams();
                if (holder.mLianmai.getVisibility() == View.GONE) {
                    if (holder.mArrow.getVisibility() == View.GONE) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, holder.mArrow.getId());
                    }
                } else {
                    params.addRule(RelativeLayout.LEFT_OF, holder.mLianmai.getId());
                }
                holder.mHand.setLayoutParams(params);
                holder.mHand.setVisibility(View.VISIBLE);
            } else {
                holder.mHand.setVisibility(View.GONE);
            }
            if (!user.getUserSetting().isAllowChat()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        holder.mGag.getLayoutParams();
                if (holder.mHand.getVisibility() == View.GONE) {
                    if (holder.mLianmai.getVisibility() == View.GONE) {
                        if (holder.mArrow.getVisibility() == View.GONE) {
                            params.addRule(RelativeLayout.ALIGN_PARENT_END);
                        } else {
                            params.addRule(RelativeLayout.LEFT_OF, holder.mArrow.getId());
                        }
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, holder.mHand.getId());
                    }
                } else {
                    params.addRule(RelativeLayout.LEFT_OF, holder.mLianmai.getId());
                }
                params.rightMargin = DensityUtil.dp2px(mContext, 10);
                holder.mGag.setLayoutParams(params);
                holder.mGag.setVisibility(View.VISIBLE);
            } else {
                holder.mGag.setVisibility(View.GONE);
            }
            if (user.getUserSetting().isAllowDraw()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        holder.mDraw.getLayoutParams();
                if (holder.mGag.getVisibility() == View.GONE) {
                    if (holder.mHand.getVisibility() == View.GONE) {
                        if (holder.mLianmai.getVisibility() == View.GONE) {
                            if (holder.mArrow.getVisibility() == View.GONE) {
                                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                            } else {
                                params.addRule(RelativeLayout.LEFT_OF, holder.mArrow.getId());
                            }
                        } else {
                            params.addRule(RelativeLayout.LEFT_OF, holder.mHand.getId());
                        }
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, holder.mLianmai.getId());
                    }
                } else {
                    params.addRule(RelativeLayout.LEFT_OF, holder.mGag.getId());
                }
                params.rightMargin = DensityUtil.dp2px(mContext, 10);
                holder.mDraw.setLayoutParams(params);
                holder.mDraw.setVisibility(View.VISIBLE);
            } else {
                holder.mDraw.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.room_user_item_layout;
    }

    @Override
    public RoomUserViewHolder getViewHolder(View itemView, int viewType) {
        return new RoomUserViewHolder(itemView);
    }

    final class RoomUserViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_user_device_icon)
        ImageView mDevice;
        @BindView(R.id.id_user_name)
        TextView mUserName;
        @BindView(R.id.id_user_identity)
        ImageView mIdentity;
        @BindView(R.id.id_user_status_lianmai)
        RelativeLayout mLianmai;
        @BindView(R.id.id_user_status_wait)
        TextView mStatusWait;
        @BindView(R.id.id_user_status_ing)
        ImageView mStatusing;
        @BindView(R.id.id_user_status_gag)
        ImageView mGag;
        //        @BindView(R.id.id_user_status_mic)
//        ImageView mMic;
        @BindView(R.id.id_user_status_draw)
        ImageView mDraw;
        @BindView(R.id.id_user_arrow)
        ImageView mArrow;
        @BindView(R.id.id_user_status_hand)
        ImageView mHand;

        RoomUserViewHolder(View itemView) {
            super(itemView);
        }
    }

}
