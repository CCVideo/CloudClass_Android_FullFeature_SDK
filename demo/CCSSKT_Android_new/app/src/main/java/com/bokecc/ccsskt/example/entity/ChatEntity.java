package com.bokecc.ccsskt.example.entity;

import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.ChatMsg;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class ChatEntity {

    /**
     * 聊天类型
     */
    private
    @ChatMsg.ChatType
    int mType;
    /**
     * 发送该条信息的用户id
     */
    private String mUserId;
    /**
     * 发送该条信息的用户名
     */
    private String mUserName;
    /**
     * 聊天内容
     */
    private String mMsg;
    /**
     * 聊天时间
     */
    private String mTime;
    /**
     * 是否是自己发送的聊天
     */
    private boolean isSelf;
    /**
     * 用户角色
     */
    private
    @CCInteractSession.Role
    int mUserRole;

    public
    @ChatMsg.ChatType
    int getType() {
        return mType;
    }

    public void setType(@ChatMsg.ChatType int type) {
        mType = type;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public
    @CCInteractSession.Role
    int getUserRole() {
        return mUserRole;
    }

    public void setUserRole(@CCInteractSession.Role int userRole) {
        mUserRole = userRole;
    }
}
