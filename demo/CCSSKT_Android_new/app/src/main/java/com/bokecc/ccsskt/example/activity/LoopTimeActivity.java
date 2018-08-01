package com.bokecc.ccsskt.example.activity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.view.ClearEditLayout;

import butterknife.BindView;

public class LoopTimeActivity extends TitleActivity<LoopTimeActivity.LoopTimeViewHolder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_max_lianmai;
    }

    @Override
    protected LoopTimeViewHolder getViewHolder(View contentView) {
        return new LoopTimeViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final LoopTimeViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.VISIBLE).rightValue("保存").
                titleStatus(TitleOptions.VISIBLE).title("轮播频率").
                onTitleClickListener(new TitleOptions.OnTitleClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }

                    @Override
                    public void onRight() {
                        final String num = holder.mInput.getText();
//                        showLoading();
//                        mInteractSession.changeRoomRotate(CCInteractSession.ROTATE_STATUS_MODIFY, Integer.valueOf(num), new CCInteractSession.AtlasCallBack<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                EventBus.getDefault().post(Integer.valueOf(num));
//                                toastOnUiThread("修改轮播频率成功");
//                                dismissLoading();
//                                finish();
//                            }
//
//                            @Override
//                            public void onFailure(String err) {
//                                toastOnUiThread(err);
//                                dismissLoading();
//                            }
//                        });
                    }
                }).
                build();
        setTitleOptions(options);
        setRightEnabled(false);
        holder.mInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        holder.mInput.showSoftboard();
        holder.mInput.setOnEditTextChangedListener(new ClearEditLayout.OnEditTextChangedListener() {
            @Override
            public void onChanged(Editable s) {
                if (s.length() > 0) {
                    setRightEnabled(true);
                } else {
                    setRightEnabled(false);
                }
            }
        });
        holder.mInput.setText(String.valueOf(mInteractSession.getRoomMaxMemberCount() > 16 ? 16 : mInteractSession.getRoomMaxStreams()));

    }

    final class LoopTimeViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_class_name_value)
        ClearEditLayout mInput;

        LoopTimeViewHolder(View view) {
            super(view);
        }

    }
}
