package com.jinju.FirmwareServiceTransfer.utils;

public class FlowLimiter {

    long timeStamp = getNowTime();
    public int capacity;
    public int rate;
    Long tokens = 0L;

    public boolean grant() {
        long now = getNowTime();
        tokens = Math.min(capacity, tokens + (now - timeStamp) * rate / 1000);
        timeStamp = now;
        if (tokens < 1) {
            return false;
        } else {
            tokens--;
            return true;
        }
    }


    static Long getNowTime() {
        return System.currentTimeMillis();
    }


    public FlowLimiter() {
        this.timeStamp = getNowTime();
    }

    public FlowLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
}
