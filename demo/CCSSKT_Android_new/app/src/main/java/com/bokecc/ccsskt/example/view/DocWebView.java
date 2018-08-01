package com.bokecc.ccsskt.example.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bokecc.sskt.IPPTView;
import com.bokecc.sskt.doc.PageInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ppt动画实现类
 * Created by wdh on 2018/4/4.
 */
public class DocWebView extends WebView implements IPPTView{

    private DocView mDocView;
    /**
     * 构造函数
     */
    public DocWebView(Context context) {
        super(context);
        initializeOptions();
    }

    /**
     * 构造函数
     */
    public DocWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeOptions();
    }

    /**
     * 初始化WebView 附带相关的选项设置
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    public void initializeOptions() {
        WebSettings settings = getSettings();

        settings.setJavaScriptEnabled(true);

        /*
         * 支持HTTPS、HTTP混合模式
         * http://blog.csdn.net/qq_16472137/article/details/54346078
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 优先渲染界面
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // 不支持缩放
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);

        /* 支持cookies 5.0以上的手机不支持自动同步第三方cookies
         *（一般都是iframe里面的页面要存储cookies操作的设置）
         * http://blog.sina.com.cn/s/blog_6e73239a0102viku.html
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        // WebView 默认都是支持cookies
        CookieManager.getInstance().setAcceptCookie(true);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    public void setDocSetVisibility(DocView mDocView){
        this.mDocView = mDocView;
    }
    /**
     * WebView 对于手势事件不做任何操作
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void setPPTBackground(final PageInfo pageInfo) {
        final String url;
        if (pageInfo.getPageUrl().startsWith("http") ||
                pageInfo.getPageUrl().startsWith("https")) {
            url = pageInfo.getPageUrl().replace(".jpg", "/index.html");
        } else {
            url = pageInfo.getPageUrl().replace(".jpg", "/index.html");
        }
        super.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mDocView.setVisibility(View.VISIBLE);
//                loadUrl("javascript:pageChange(" + jsonObject.toString() + ")");
                loadUrl(url);
            }
        });
    }

    @Override
    public void setPPTHistory(final JSONObject jsonObject) {
        final boolean[] isflag = {true};
        super.post(new Runnable() {

            @Override
            public void run() {
                setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if(isflag[0]){
                            view.loadUrl("javascript:on_cc_live_dw_animation_change(" + jsonObject.toString() + ")");
//                            view.loadUrl("javascript:animationChange(" + jsonObject.toString() + ")");
                            isflag[0] = false;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void setPPTDocBackground(final JSONObject jsonObject) {
        // 使用WebView做文档动画的展示
            super.post(new Runnable() {
                @Override
                public void run() {
                    setVisibility(View.VISIBLE);
//                    loadUrl("javascript:animationChange(" + jsonObject.toString() + ")");
                    loadUrl("javascript:on_cc_live_dw_animation_change(" + jsonObject.toString() + ")");
                }
            });
    }

    @Override
    public void setNOPPTDocBackground() {
        post(new Runnable() {
            @Override
            public void run() {
                setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }
                });
            }
        });
    }

    @Override
    public void setDocBackground() {
        super.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
//                mDocView.setVisibility(View.VISIBLE);
                loadUrl("https://image.csslcloud.net/dp/dp.html?displayMode=1");
            }
        });
    }

}
