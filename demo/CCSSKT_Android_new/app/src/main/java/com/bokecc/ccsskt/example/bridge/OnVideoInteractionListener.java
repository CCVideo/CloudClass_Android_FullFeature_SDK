package com.bokecc.ccsskt.example.bridge;

import com.bokecc.sskt.SubscribeRemoteStream;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public interface OnVideoInteractionListener {
    SubscribeRemoteStream getStream(int position);
}
