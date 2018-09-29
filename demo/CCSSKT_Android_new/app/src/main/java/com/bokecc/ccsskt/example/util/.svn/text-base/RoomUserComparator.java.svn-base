package com.bokecc.ccsskt.example.util;


import com.bokecc.ccsskt.example.entity.RoomUser;

import java.util.Comparator;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class RoomUserComparator implements Comparator<RoomUser> {

    @Override
    public int compare(RoomUser o1, RoomUser o2) {
        try {
            long curRequestTime = (long) Double.parseDouble(o1.getUser().getRequestTime());
            long compareRequestTime = (long) Double.parseDouble(o2.getUser().getRequestTime());
            return (int) (curRequestTime - compareRequestTime);
        } catch (Exception e) {
            return 1;
        }
    }

}
