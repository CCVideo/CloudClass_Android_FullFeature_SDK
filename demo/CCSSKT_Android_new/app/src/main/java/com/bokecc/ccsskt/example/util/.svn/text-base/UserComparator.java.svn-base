package com.bokecc.ccsskt.example.util;

import com.bokecc.sskt.bean.User;

import java.util.Comparator;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class UserComparator implements Comparator<User> {

    @Override
    public int compare(User o1, User o2) {
        long curRequestTime = (long) Double.parseDouble(o1.getRequestTime());
        long compareRequestTime = (long) Double.parseDouble(o2.getRequestTime());
        return (int) (curRequestTime - compareRequestTime);
    }
}
