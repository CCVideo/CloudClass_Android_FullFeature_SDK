package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.IconAdapter;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;
import com.bokecc.ccsskt.example.entity.IconEntity;
import com.bokecc.ccsskt.example.entity.VideoStreamView;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.sskt.CCInteractSession;

import java.util.ArrayList;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class BottomIconPopup extends BasePopupWindow {

    private final static String TAG = BottomIconPopup.class.getSimpleName();
    private IconAdapter mIconAdapter;
    private int mPosition = -1;
    private OnChooseClickListener mOnChooseClickListener;

    private VideoStreamView mVideoStreamView;
    private int mPopupStreamPosition;

    private static final int resIds[] = new int[]{
            R.drawable.auth_draw_normal,  R.drawable.close_camera_normal, R.drawable.close_mic_normal,
            R.drawable.kickout_normal, R.drawable.video_fullscreen_normal,R.drawable.setup_teacher
    };
    private static final String resDess[] = new String[]{
            "授权标注", /*"不轮播",*/ "关闭视频", "关麦", "踢下麦", "全屏视频","设为讲师"
    };

    private ArrayList<IconEntity> mIconEntities;
    private boolean haveLoop = false, haveVideo = true;

    public BottomIconPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {

        mIconEntities = new ArrayList<>();
        for (int i = 0; i < resIds.length; i++) {
            IconEntity iconEntity = new IconEntity(resIds[i], resDess[i]);
            mIconEntities.add(iconEntity);
        }

        findViewById(R.id.id_bottom_icon_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final RecyclerView icons = findViewById(R.id.id_bottom_icons);
        icons.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        icons.addOnItemTouchListener(new BaseOnItemTouch(icons, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                dismiss();
                mPosition = icons.getChildAdapterPosition(viewHolder.itemView);
            }
        }));
        mIconAdapter = new IconAdapter(mContext);
        mIconAdapter.bindDatas(mIconEntities);
        icons.setAdapter(mIconAdapter);

        setOnPopupDismissListener(new OnPopupDismissListener() {
            @Override
            public void onDismiss() {
                if (mOnChooseClickListener != null && mPosition != -1) {
                    mOnChooseClickListener.onClick(mPosition, mIconEntities.get(mPosition).getResId(),
                            mVideoStreamView);
                    mPosition = -1;
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.bottom_icon_layout;
    }

    public int getPopupStreamPosition() {
        return mPopupStreamPosition;
    }

    public void show(View view, int position, VideoStreamView videoStreamView) {
        if (videoStreamView == null) {
            return;
        }
        mPopupStreamPosition = position;
        mVideoStreamView = videoStreamView;
        if (videoStreamView.getStream().isAllowDraw()) {
            update(0, new IconEntity(R.drawable.auth_draw_cancel_normal, "取消授权"));
        } else {
            update(0, new IconEntity(R.drawable.auth_draw_normal, "授权标注"));
        }
//        if (CCInteractSession.getInstance().getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
//            if (!haveLoop) {
//                if (videoStreamView.getStream().isLock()) {
//                    add(1, new IconEntity(R.drawable.unlock_normal, "参与轮播"));
//                } else {
//                    add(1, new IconEntity(R.drawable.lock_normal, "不轮播"));
//                }
//            } else {
//                if (videoStreamView.getStream().isLock()) {
//                    update(1, new IconEntity(R.drawable.unlock_normal, "参与轮播"));
//                } else {
//                    update(1, new IconEntity(R.drawable.lock_normal, "不轮播"));
//                }
//            }
//        } else {
//            if (haveLoop) {
//                remove(1);
//                haveLoop = false;
//            }
//        }
        int index;
        if (haveLoop) {
            index = 2;
        } else {
            index = 1;
        }
        if (videoStreamView.getStream().isAllowVideo()) {
            update(index, new IconEntity(R.drawable.close_camera_normal, "关闭视频"));
        } else {
            update(index, new IconEntity(R.drawable.open_camera_normal, "开放视频"));
        }
        if (haveLoop) {
            index = 3;
        } else {
            index = 2;
        }
        if (videoStreamView.getStream().isAllowAudio()) {
            update(index, new IconEntity(R.drawable.close_mic_normal, "关麦"));
        } else {
            update(index, new IconEntity(R.drawable.open_mic_normal, "开麦"));
        }
        if (CCInteractSession.getInstance().getTemplate() == CCInteractSession.TEMPLATE_TILE || position == -1) {
            if (haveVideo) {
                remove(mIconEntities.size()-2);
                haveVideo = false;
            }
            if(mIconEntities.size() == 6) {
                remove(mIconEntities.size()-2);
            }
        } else {
            if (!haveVideo) {
                if (CCInteractSession.getInstance().getTemplate() == CCInteractSession.TEMPLATE_SPEAK) {
                
                    add(mIconEntities.size()-1, new IconEntity(R.drawable.video_fullscreen_normal, "全屏视频"));
                } else if(CCInteractSession.getInstance().getTemplate() == CCInteractSession.TEMPLATE_SINGLE){
                    add(mIconEntities.size()-1, new IconEntity(R.drawable.video_fullscreen_normal, "主视频"));
                }
            } else {
                if (CCInteractSession.getInstance().getTemplate() == CCInteractSession.TEMPLATE_SPEAK) {
                    update(mIconEntities.size()-2, new IconEntity(R.drawable.video_fullscreen_normal, "全屏视频"));
                } else if (CCInteractSession.getInstance().getTemplate() == CCInteractSession.TEMPLATE_SINGLE){
                    update(mIconEntities.size()-2, new IconEntity(R.drawable.video_fullscreen_normal, "主视频"));
                }
            }
        }
        if(mIconEntities.size() > 6) {
            if (CCInteractSession.getInstance().getTemplate() == CCInteractSession.TEMPLATE_SPEAK) {
                remove(mIconEntities.size()-2);
            } else if(CCInteractSession.getInstance().getTemplate() == CCInteractSession.TEMPLATE_SINGLE){
                remove(mIconEntities.size()-2);
                update(mIconEntities.size()-2, new IconEntity(R.drawable.video_fullscreen_normal, "主视频"));
            }
        }
        if (videoStreamView.getStream().isSetupTeacher()) {
            update(mIconEntities.size()-1, new IconEntity(R.drawable.setup_teacher_canclenormal, "撤销讲师"));
        } else {
            update(mIconEntities.size()-1, new IconEntity(R.drawable.setup_teacher, "设为讲师"));
        }
        super.show(view);
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefTranslateEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefTranslateExitAnim();
    }

    public void setOnChooseClickListener(OnChooseClickListener onChooseClickListener) {
        mOnChooseClickListener = onChooseClickListener;
    }

    public void update(int index, IconEntity iconEntity) {
        mIconAdapter.update(index, iconEntity);
    }

    public void remove(int index) {
        mIconAdapter.remove(index);
    }

    public void add(int index, IconEntity iconEntity) {
        mIconAdapter.add(index, iconEntity);
    }

    public interface OnChooseClickListener {
        void onClick(int index, int tag, VideoStreamView videoStreamView);
    }

}
