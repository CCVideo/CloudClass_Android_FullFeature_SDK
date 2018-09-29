package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.MoreAdapter;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;
import com.bokecc.ccsskt.example.entity.MoreItem;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;

import java.util.ArrayList;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class MorePopup extends BasePopupWindow {

    private RecyclerView mMoreList;
    private MoreAdapter mMoreAdapter;

    private OnMoreItemClickListener mOnMoreItemClickListener;
    private int mPosition = -1;

    public MorePopup(Context context) {
        super(context);
    }

    public MorePopup(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    protected void onViewCreated() {
        mMoreList = findViewById(R.id.id_more_list);
        mMoreList.setLayoutManager(new LinearLayoutManager(mContext));
        mMoreAdapter = new MoreAdapter(mContext);
        mMoreList.setAdapter(mMoreAdapter);
        mMoreList.addOnItemTouchListener(new BaseOnItemTouch(mMoreList, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                dismiss();
                mPosition = mMoreList.getChildAdapterPosition(viewHolder.itemView);
            }
        }));
        setOnPopupDismissListener(new OnPopupDismissListener() {
            @Override
            public void onDismiss() {
                if (mOnMoreItemClickListener != null && mPosition != -1) {
                    mOnMoreItemClickListener.onClick(mMoreAdapter.getDatas().get(mPosition).getAction());
                    mPosition = -1;
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.more_popup_layut;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getRightScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getRightScaleExitAnim();
    }

    public void removeItem(int position) {
        mMoreAdapter.getDatas().remove(position);
        mMoreAdapter.notifyDataSetChanged();
    }

    public void addItem(int position, MoreItem moreItem) {
        mMoreAdapter.getDatas().add(position, moreItem);
        mMoreAdapter.notifyDataSetChanged();
    }

    public void setMoreItems(ArrayList<MoreItem> moreItems) {
        mMoreAdapter.bindDatas(moreItems);
        mMoreAdapter.notifyDataSetChanged();
    }

    public void setOnMoreItemClickListener(OnMoreItemClickListener onMoreItemClickListener) {
        mOnMoreItemClickListener = onMoreItemClickListener;
    }

    public interface OnMoreItemClickListener {
        void onClick(int position);
    }

}
