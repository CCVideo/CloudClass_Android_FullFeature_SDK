package com.bokecc.ccsskt.example.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationTargetException;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class SoftKeyboardUtil {

    private static final String TAG = SoftKeyboardUtil.class.getSimpleName();

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private InputMethodManager imm;
    private int keyboardHeight = 0;// 软键盘的高度
    private int navigationBarHeight;// 虚拟按键的高度

    public SoftKeyboardUtil(Context context) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 监听键盘高度和键盘时候处于打开状态，在调用的Activity中的onDestroy()方法中调用
     * 该类中的removeGlobalOnLayoutListener()方法来移除监听
     *
     */
    public void observeSoftKeyboard(final Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean isShowKeyboard;// 软键盘的显示状态
            @Override
            public void onGlobalLayout() {
                onGlobalLayoutListener = this;

                // 应用可以显示的区域。此处包括应用占用的区域，
                // 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
                Rect r = new Rect();
                decorView.getWindowVisibleDisplayFrame(r);
                // 屏幕高度。这个高度不含虚拟按键的高度
                int screenHeight = decorView.getRootView().getHeight();
                //虚拟按键
                NavigationBarInfo navigationBarInfo = getNavigationBarInfo(activity);
                if (navigationBarInfo.isHasNavigationBar()) {
//                    navigationBarHeight = navigationBarInfo.getmPoint().y;
                    if (isLandscape(activity)) {
                        navigationBarHeight = Math.min(navigationBarInfo.getmPoint().x,navigationBarInfo.getmPoint().y);
                    } else {
                        navigationBarHeight = navigationBarInfo.getmPoint().y;
                    }
                }else {
                    navigationBarHeight = 0;
                }
                int heightDiff = screenHeight - r.bottom;
                // 在不显示软键盘时，heightDiff等于虚拟按键高度。
                // 在显示软键盘时，heightDiff会变大，等于软键盘+虚拟按键高度。
                // 所以heightDiff大于虚拟按键高度时表示软键盘出现了，
                // 这时可算出软键盘的高度，即heightDiff减去虚拟按键的高度
                int temp = 0;
                boolean isChanged = false;
                if (heightDiff > navigationBarHeight) {
                    temp = keyboardHeight;
                    keyboardHeight = heightDiff - navigationBarHeight;
                    isChanged = temp != keyboardHeight;
                }
                if (isShowKeyboard) {
                    // 如果软键盘是弹出的状态，并且heightDiff小于等于虚拟按键高度，
                    // 说明这时软键盘已经收起
                    if (heightDiff <= navigationBarHeight) {
                        isShowKeyboard = false;
                        listener.onSoftKeyBoardChange(keyboardHeight, false, 0);
                    } else {
                        if (isChanged) {
                            // 如果软键盘高度发生变化，进行通知
                            listener.onSoftKeyBoardChange(keyboardHeight, true, temp-keyboardHeight);
                        }
                    }
                } else {
                    // 如果软键盘是收起的状态，并且heightDiff大于虚拟按键高度，
                    // 说明这时软键盘已经弹出
                    if (heightDiff > navigationBarHeight) {
                        isShowKeyboard = true;
                        listener.onSoftKeyBoardChange(keyboardHeight, true, 0);
                    }
                }
            }
        });
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow, int changeHeight);
    }

    public void removeGlobalOnLayoutListener(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        if (onGlobalLayoutListener != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                decorView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
            } else {
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }
    }

    /**
     * 获得状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取 NavigationBar 的高度，虚拟按键的高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public void hideKeyboard(View view) {
        view.setFocusableInTouchMode(false);
        view.setFocusable(false);
        view.clearFocus();
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(View view) {
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.requestFocus();
        imm.showSoftInput(view, 0);
    }

    private static class NavigationBarInfo {
        private Point mPoint;
        private int orientation;//0:horizontal 1:vertical
        private boolean isHasNavigationBar;

        NavigationBarInfo() {
        }

        Point getmPoint() {
            return mPoint;
        }

        void setmPoint(Point mPoint) {
            this.mPoint = mPoint;
        }

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        boolean isHasNavigationBar() {
            return isHasNavigationBar;
        }

        void setHasNavigationBar(boolean hasNavigationBar) {
            isHasNavigationBar = hasNavigationBar;
        }
    }

    private static NavigationBarInfo getNavigationBarInfo(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);
        NavigationBarInfo navigationBarInfo = new NavigationBarInfo();
        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            navigationBarInfo.setmPoint(new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y));
            navigationBarInfo.setOrientation(1);
            navigationBarInfo.setHasNavigationBar(true);
            return navigationBarInfo;
        }
        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            navigationBarInfo.setmPoint(new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y));
            navigationBarInfo.setOrientation(0);
            navigationBarInfo.setHasNavigationBar(true);
            return navigationBarInfo;
        }
        // navigation bar is not present
        navigationBarInfo.setmPoint(new Point());
        navigationBarInfo.setOrientation(0);
        navigationBarInfo.setHasNavigationBar(false);
        return navigationBarInfo;

    }

    private static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }
        return size;
    }
    public boolean isLandscape(Context context) {
        boolean isLannds = false;
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            isLannds = true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            isLannds = false;
        }
        return isLannds;
    }
}
