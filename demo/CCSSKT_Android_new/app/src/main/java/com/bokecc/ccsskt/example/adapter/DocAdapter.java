package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bokecc.ccsskt.example.view.SwipeMenuLayout;
import com.bokecc.sskt.doc.DocInfo;
import com.bumptech.glide.Glide;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class DocAdapter extends BaseRecycleAdapter<DocAdapter.DocViewHolder, DocInfo> {

    private OnDelOnClickListener mOnDelOnClickListener;

    public DocAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(DocViewHolder holder, int position) {
        final DocInfo docInfo = mDatas.get(position);
        holder.mName.setText(docInfo.getName());
        holder.mSize.setText(Formatter.formatFileSize(mContext, docInfo.getSize()));
        Glide.with(mContext).load(docInfo.getThumbnailsUrl()).fitCenter().into(holder.mIcon);
        holder.mDel.setOnClickListener(new DelOnClicklistener(position, docInfo));
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.doc_item_layout;
    }

    @Override
    public DocViewHolder getViewHolder(View itemView, int viewType) {
        return new DocViewHolder(itemView);
    }

    public void setOnDelOnClickListener(OnDelOnClickListener onDelOnClickListener) {
        mOnDelOnClickListener = onDelOnClickListener;
    }

    public interface OnDelOnClickListener {
        void onDel(int position, DocInfo docInfo);
    }

    private class DelOnClicklistener implements View.OnClickListener {

        private int mPosition;
        private DocInfo mDocInfo;

        DelOnClicklistener(int position, DocInfo docInfo) {
            mPosition = position;
            mDocInfo = docInfo;
        }

        @Override
        public void onClick(View v) {
            if (mOnDelOnClickListener != null) {
                mOnDelOnClickListener.onDel(mPosition, mDocInfo);
            }
        }
    }

    final class DocViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_doc_item_swipe)
        SwipeMenuLayout mSwipeMenuLayout;
        @BindView(R.id.id_doc_item_icon)
        ImageView mIcon;
        @BindView(R.id.id_doc_item_name)
        TextView mName;
        @BindView(R.id.id_doc_item_size)
        TextView mSize;
        @BindView(R.id.id_doc_item_del)
        Button mDel;

        DocViewHolder(View itemView) {
            super(itemView);
        }
    }

}
