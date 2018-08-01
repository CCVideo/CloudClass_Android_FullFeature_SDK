package com.bokecc.ccsskt.example.bridge;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public interface OnDocInteractionListener {
    void toggleTopLayout(boolean isOnlyDoc);
    void docFullScreen();
    void exitDocFullScreen();
    void videoFullScreen();
    void exitVideoFullScreen();
}
