package com.cqq.stock.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

/**
 * 时间计算工具
 */
public class TimeUtil {

    private TimeUtil() {
    }

    /**
     * 获取时间的int类型 ， 如 20191101
     *
     * @return 返回当前时间的date数字
     */
    public static int getDatetime() {
        String yyyyMMdd = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return Integer.parseInt(yyyyMMdd);
    }

    /**
     * @return 2019 今年的年份
     */
    public static int getThisYearNumber() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * @return 2018 去年的年份
     */
    public static int getLastYearNumber() {
        return Calendar.getInstance().get(Calendar.YEAR) - 1;
    }


    /**
     * 返回今年的第一天的数字
     *
     * @return 2019_01_01
     */
    public static int getThisYearBeginDate() {
        return getThisYearNumber() * 10000 + 101;
    }

    /**
     * 返回今年的最后一天的数字
     *
     * @return 2019_12_31
     */
    public static int getThisYearEndDate() {
        return getThisYearNumber() * 10000 + 1231;
    }

    /**
     * 返回去年的第一天的数字
     *
     * @return 2019_01_01
     */
    public static int getLastYearBeginDate() {
        return getThisYearNumber() * 10000 + 101 - 10000;
    }

    /**
     * 返回去年的第一天的数字
     *
     * @return 2019_01_01
     */
    public static int getLastYearEndDate() {
        return getThisYearNumber() * 10000 + 1231 - 10000;
    }


    public static <T, U> T doingSomething(U u, Function<U, T> function, String flag) {
        long spendTime = System.currentTimeMillis();
        T apply = function.apply(u);
        long spendTime2 = System.currentTimeMillis();
        System.out.println(flag + "-spendTime:" + (spendTime2 - spendTime));
        return apply;


    }


    // 2019_12_23

    /**
     * 向前偏移多少天
     * @param date  20200308
     * @param basis 7
     * @return 20200301
     */
    public static long offsetLeft(long date, int basis) {
        Calendar instance = Calendar.getInstance();
        int month = (int) ((date / 100) % 100) - 1;
        instance.set((int) (date / 10000), month, (int) (date % 100));
        long timeInMillis = instance.getTimeInMillis();
        timeInMillis -= basis * 24 * 60 * 60 * 1000L;
        Date date1 = new Date();
        date1.setTime(timeInMillis);
        String yyyyMMdd = new SimpleDateFormat("yyyyMMdd").format(date1);
        return Long.parseLong(yyyyMMdd);
    }
}
