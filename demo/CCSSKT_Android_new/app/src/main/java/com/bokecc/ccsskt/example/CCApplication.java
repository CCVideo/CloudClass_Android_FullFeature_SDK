package com.bokecc.ccsskt.example;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.interact.InteractSessionManager;
import com.bokecc.ccsskt.example.interact.MyBroadcastReceiver;
import com.bokecc.sskt.CCInteractSDK;
import com.bokecc.sskt.CCInteractSession;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.ref.WeakReference;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public class CCApplication extends Application {

    private static final String TAG = "CCApp";

    public static int mAppStatus = -1; // 表示 force_kill
    private static WeakReference<Context> context;

    public static int sClassDirection = 0; // 课堂模式（方向）0竖屏 1横屏

    public static long mNamedTimeEnd = 0L;
    public static int mNamedTotalCount = 0;
    public static int mNamedCount = 0;

    public static String mFisrtCityName;//城市节点的名称
    public static String mAreaCode;//城市的区域。

    public static boolean isConnect = false;

    private int mActivityCount = 0, mCount;
    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = new WeakReference<Context>(this);
        }

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        CCInteractSDK.init(this.getApplicationContext(), true);
        CrashReport.initCrashReport(this, "91c6b832a1", true);
        MyBroadcastReceiver.getInstance().initial(audioManager);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.i(TAG, "onActivityCreated: " );
                mActivityCount++;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i(TAG, "onActivityStarted: " );
                if (mCount == 0) {
                    Log.i(TAG, "**********切到前台**********");
//                    CCInteractSession.getInstance().switchCamera(null, new CCInteractSession.AtlasCallBack<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            CCInteractSession.getInstance().enableVideo(false);
//                        }
//
//                        @Override
//                        public void onFailure(String err) {
//
//                        }
//                    });
                }
                mCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i(TAG, "onActivityResumed: " );
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i(TAG, "onActivityPaused: " );
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.i(TAG, "onActivityStopped: " );
                mCount--;
                if (mCount == 0) {
//                    Log.i(TAG, "**********切到后台**********");
//                    CCInteractSession.getInstance().disableVideo(false);
//                    CCInteractSession.getInstance().switchCamera(null, null);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.i(TAG, "onActivitySaveInstanceState: " );
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.i(TAG, "onActivityDestroyed: ");
                mActivityCount--;
                if (mActivityCount <= 0) {
                    InteractSessionManager.getInstance().reset();
                    CCInteractSession.getInstance().releaseAll();
                }
            }
        });
    }

    public static Context getContext() {
        return context == null ? null : context.get();
    }

    public static String getVersion() {
        try {
            PackageManager manager = context.get().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.get().getPackageName(), 0);
            return "v" + info.versionName;
        } catch (Exception e) {
            return "v" + Config.APP_VERSION;
        }
    }

}
