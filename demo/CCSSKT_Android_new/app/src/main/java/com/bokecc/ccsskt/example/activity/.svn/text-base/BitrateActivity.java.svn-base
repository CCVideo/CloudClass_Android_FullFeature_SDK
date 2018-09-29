package com.bokecc.ccsskt.example.activity;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;

import butterknife.BindView;
import butterknife.OnClick;

public class BitrateActivity extends TitleActivity<BitrateActivity.BitrateViewHolder> {

    private static final int MIN_PROGRESS = 20;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_bitrate;
    }

    @Override
    protected BitrateViewHolder getViewHolder(View contentView) {
        return new BitrateViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final BitrateViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("码率设置").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        aboutRoleBitrate(holder.mTCurValue, holder.mTBar, mInteractSession.getPresenterBitrate());
        aboutRoleBitrate(holder.mSCurValue, holder.mSBar, mInteractSession.getTalkerBitrate());

    }

    private void aboutRoleBitrate(final TextView curValue, SeekBar bar, int value) {
        bar.setMax(800-MIN_PROGRESS);
        curValue.setText(value + "K");
        bar.setProgress(value);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                curValue.setText((MIN_PROGRESS + progress) + "K");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                curValue.setText((MIN_PROGRESS + seekBar.getProgress()) + "K");
            }
        });
    }

    final class BitrateViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_bitrate_tcurrent_value)
        TextView mTCurValue;
        @BindView(R.id.id_bitrate_scurrent_value)
        TextView mSCurValue;
        @BindView(R.id.id_bitrate_teacher_bar)
        SeekBar mTBar;
        @BindView(R.id.id_bitrate_student_bar)
        SeekBar mSBar;

        BitrateViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.id_bitrate_save)
        void saveBitrate() {
            showLoading();
            updateStudent();
        }

    }

    private void updateStudent() {
//        mInteractSession.changeRoomStudentBitrate(MIN_PROGRESS + mViewHolder.mSBar.getProgress(), new CCInteractSession.AtlasCallBack<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                updateTeacher();
//            }
//
//            @Override
//            public void onFailure(String err) {
//                dismissLoading();
//                toastOnUiThread(err);
//            }
//        });
    }

    private void updateTeacher() {
//        mInteractSession.changeRoomTeacherBitrate(MIN_PROGRESS + mViewHolder.mTBar.getProgress(), new CCInteractSession.AtlasCallBack<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                dismissLoading();
//                finish();
//            }
//
//            @Override
//            public void onFailure(String err) {
//                dismissLoading();
//                toastOnUiThread(err);
//            }
//        });
    }

}
