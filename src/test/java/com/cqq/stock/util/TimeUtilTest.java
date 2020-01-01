package com.cqq.stock.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeUtilTest {

    @Test
    public void getDatetime() {
        int datetime = TimeUtil.getDatetime();
        assertEquals(datetime, 20191017);
    }

    @Test
    public void offset() {
        long offset = TimeUtil.offset(2019_05_06, 60);
        System.out.println(offset);
    }
}