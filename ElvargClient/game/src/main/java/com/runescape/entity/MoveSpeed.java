package com.runescape.entity;

import com.runescape.util.SerialEnum;

public enum MoveSpeed implements SerialEnum {
   CRAWL((byte)0),
    WALK((byte)1),
    RUN((byte)2),
    STATIONARY((byte)-1);

    public byte speed;

    MoveSpeed(byte arg0) {
        this.speed = arg0;
    }

    public int id() {
        return this.speed;
    }
}
