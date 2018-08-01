package com.bokecc.ccsskt.example.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.DocAdapter;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.recycle.RecycleViewDivider;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.doc.DocInfo;
import com.bokecc.sskt.doc.RoomDocs;

import butterknife.BindView;
import butterknife.OnClick;

public class DocListActivity extends TitleActivity<DocListActivity.DocListViewHolder> {

    private DocAdapter mDocAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_doc_list;
    }

    @Override
    protected void beforeSetContentView() {
        if (CCApplication.sClassDirection == 1) {
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
    protected DocListViewHolder getViewHolder(View contentView) {
        return new DocListViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final DocListViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("提取文档").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mDocAdapter = new DocAdapter(this);
        holder.mEmptyLayout.setVisibility(View.VISIBLE);
        holder.mDocs.setVisibility(View.GONE);
        holder.mDocs.setLayoutManager(new LinearLayoutManager(this));
        holder.mDocs.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, 1, Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BOTTOM));
        holder.mDocs.setAdapter(mDocAdapter);
        fetchRoomDocs();
        holder.mDocs.addOnItemTouchListener(new BaseOnItemTouch(holder.mDocs, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                int position = holder.mDocs.getChildAdapterPosition(viewHolder.itemView);
                selectDoc(position);
            }
        }));
        mDocAdapter.setOnDelOnClickListener(new DocAdapter.OnDelOnClickListener() {
            @Override
            public void onDel(int position, DocInfo docInfo) {
                deleteDoc(position, docInfo);
            }
        });

    }

    /**
     * 选中指定位置的文档
     */
    private void selectDoc(int position) {
        DocInfo docInfo = mDocAdapter.getDatas().get(position);
        Intent data = new Intent();
        data.putExtra("selected_doc", docInfo);
        setResult(Config.DOC_LIST_RESULT_CODE, data);
        finish();
    }

    /**
     * 获取直播间文档列表
     */
    private void fetchRoomDocs() {
        showProgress();
        mInteractSession.getRoomDocs(null, new CCInteractSession.AtlasCallBack<RoomDocs>() {
            @Override
            public void onSuccess(final RoomDocs roomDocs) {
                dismissProgress();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (roomDocs.getDoneDocs().size() > 0) {
                            mViewHolder.mDocs.setVisibility(View.VISIBLE);
                            mViewHolder.mEmptyLayout.setVisibility(View.GONE);
                            mDocAdapter.bindDatas(roomDocs.getDoneDocs());
                            mDocAdapter.notifyDataSetChanged();
                        } else {
                            mViewHolder.mDocs.setVisibility(View.GONE);
                            mViewHolder.mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String err) {
                Log.e("tag", "fetch onFailure: " + err);
                dismissProgress();
                toastOnUiThread(err);
            }
        });
    }

    /**
     * 删除指定文档
     */
    private void deleteDoc(final int positon, final DocInfo docInfo) {
        showLoading();
        mInteractSession.delDoc(docInfo.getRoomId(), docInfo.getDocId(), new CCInteractSession.AtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissLoading();
                mEventBus.post(new MyEBEvent(Config.DOC_DEL, docInfo.getDocId()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDocAdapter.getDatas().remove(positon);
                        mDocAdapter.notifyItemRemoved(positon);
                        if (positon != mDocAdapter.getDatas().size()) {
                            mDocAdapter.notifyItemRangeChanged(positon, mDocAdapter.getDatas().size() - positon);
                        }
                        if (mDocAdapter.getDatas().size() <= 0) {
                            mViewHolder.mDocs.setVisibility(View.GONE);
                            mViewHolder.mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String err) {
                Log.e("tag", "onFailure: " + err);
                dismissLoading();
                toastOnUiThread(err);
            }
        });
    }

    final class DocListViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_doc_list)
        RecyclerView mDocs;
        @BindView(R.id.id_doc_list_empty)
        RelativeLayout mEmptyLayout;

         DocListViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.id_doc_list_pan_btn)
        void getFromPan() {
            // TODO: 2017/8/7
        }

    }

}
