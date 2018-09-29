package com.bokecc.ccsskt.example.popup;

import android.content.Context;
import android.view.animation.Animation;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BasePopupWindow;
import com.bokecc.ccsskt.example.base.PopupAnimUtil;


/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class LoadingPopup extends BasePopupWindow {

    public LoadingPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {
    }

    @Override
    protected int getContentView() {
        return R.layout.loading_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }
}
