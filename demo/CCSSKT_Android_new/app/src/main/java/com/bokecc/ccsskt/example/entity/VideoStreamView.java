package com.bokecc.ccsskt.example.entity;

import com.bokecc.sskt.SubscribeRemoteStream;
import com.bokecc.sskt.base.renderer.CCSurfaceRenderer;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class VideoStreamView {

    private SubscribeRemoteStream mStream;
    private CCSurfaceRenderer mRenderer;

    public SubscribeRemoteStream getStream() {
        return mStream;
    }

    public void setStream(SubscribeRemoteStream stream) {
        mStream = stream;
    }

    public CCSurfaceRenderer getRenderer() {
        return mRenderer;
    }

    public void setRenderer(CCSurfaceRenderer renderer) {
        mRenderer = renderer;
    }

}