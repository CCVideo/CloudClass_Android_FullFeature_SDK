package com.bokecc.ccsskt.example.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.ImgAdapter;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;

import java.util.ArrayList;

import butterknife.BindView;

public class DocImgGridActivity extends TitleActivity<DocImgGridActivity.DocImgGridViewHolder> {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_doc_img_grid;
    }

    @Override
    protected DocImgGridViewHolder getViewHolder(View contentView) {
        return new DocImgGridViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final DocImgGridViewHolder holder) {
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("跳转页面").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        ArrayList<String> allImgUrls = getIntent().getExtras().getStringArrayList("doc_img_list");
        ImgAdapter imgAdapter = new ImgAdapter(this);
        holder.mImgGrid.setLayoutManager(new GridLayoutManager(this, 4));
        holder.mImgGrid.setAdapter(imgAdapter);
        imgAdapter.bindDatas(allImgUrls);
        holder.mImgGrid.addOnItemTouchListener(new BaseOnItemTouch(holder.mImgGrid, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                int position = holder.mImgGrid.getChildAdapterPosition(viewHolder.itemView);
                Intent data = new Intent();
                data.putExtra("doc_img_grid_position", position);
                setResult(Config.DOC_GRID_RESULT_CODE, data);
                finish();
            }
        }));

    }

    final class DocImgGridViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_doc_imgs)
        RecyclerView mImgGrid;

        DocImgGridViewHolder(View view) {
            super(view);
        }
    }

}
