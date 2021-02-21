package com.cqq.stock.util;


public class TimingClock {
    private long time;

    public TimingClock(String msg) {
        System.out.println(msg);
        time = System.currentTimeMillis();
    }

    public TimingClock() {
        time = System.currentTimeMillis();
    }

    public void call(String msg) {
        long now = System.currentTimeMillis();
        System.out.println(msg +":"+ (now - time) + "ms");
        this.time = now;

    }
}
