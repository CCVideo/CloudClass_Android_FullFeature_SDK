package com.bokecc.ccsskt.example.activity;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.view.ClearEditLayout;
import com.bokecc.ccsskt.example.util.ParseMsgUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class LinkActivity extends TitleActivity<LinkActivity.LinkViewHolder> {


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_link;
    }

    @Override
    protected LinkViewHolder getViewHolder(View contentView) {
        return new LinkViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(LinkViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("复制课堂地址").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        holder.mClearEditLayout.setHint(getResources().getString(R.string.link_url));
        holder.mClearEditLayout.setHintColor(Color.parseColor("#cccccc"));

    }

    final class LinkViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_link_url)
        ClearEditLayout mClearEditLayout;

        public LinkViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.id_link_go)
        void go() {
            String url = mClearEditLayout.getText();
            if (TextUtils.isEmpty(url)) {
                showToast("请输入链接");
                return;
            }
            parseUrl(url);
        }

        private void parseUrl(String url) {
            ParseMsgUtil.parseUrl(LinkActivity.this, url, new ParseMsgUtil.ParseCallBack() {
                @Override
                public void onStart() {
                    showProgress();
                }

                @Override
                public void onSuccess() {
                    dismissProgress();
                }

                @Override
                public void onFailure(String err) {
                    toastOnUiThread(err);
                    dismissProgress();
                }
            });
        }

    }

}
