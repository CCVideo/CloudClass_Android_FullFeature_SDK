package com.bokecc.ccsskt.example.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BaseActivity;
import com.bokecc.ccsskt.example.global.Config;


public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CCApplication.mAppStatus = 0; // 当前状态正常
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewCreated() {
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                go(HomeActivity.class);
                finish();
            }
        }, Config.SPLASH_DELAY);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

}
