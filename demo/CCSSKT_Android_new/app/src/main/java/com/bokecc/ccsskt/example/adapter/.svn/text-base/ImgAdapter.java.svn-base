package com.bokecc.ccsskt.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.recycle.BaseRecycleAdapter;
import com.bumptech.glide.Glide;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class ImgAdapter extends BaseRecycleAdapter<ImgAdapter.DocImgViewHolder, String> {

    public ImgAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final DocImgViewHolder holder, int position) {
       Glide.with(mContext).load(mDatas.get(position)).
               asBitmap().
               into(holder.mDocImg);
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.doc_img_layout;
    }

    @Override
    public DocImgViewHolder getViewHolder(View itemView, int viewType) {
        return new DocImgViewHolder(itemView);
    }

    final class DocImgViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_doc_img)
        ImageView mDocImg;

        DocImgViewHolder(View itemView) {
            super(itemView);
        }
    }

}
