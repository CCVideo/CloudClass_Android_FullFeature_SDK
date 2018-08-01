package com.bokecc.ccsskt.example.entity;

import com.bokecc.sskt.bean.User;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class RoomUser {
    private User mUser;
    private int mMaiIndex = -1; // 麦序

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public int getMaiIndex() {
        return mMaiIndex;
    }

    public void setMaiIndex(int maiIndex) {
        mMaiIndex = maiIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomUser roomUser = (RoomUser) o;

        return mUser != null ? mUser.equals(roomUser.mUser) : roomUser.mUser == null;

    }

    @Override
    public int hashCode() {
        return mUser != null ? mUser.hashCode() : 0;
    }
}
