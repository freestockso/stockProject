package com.cqq.stock.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

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
        return Integer.valueOf(yyyyMMdd);
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

    public static <T, U> T doingSomething(U u, Function<U, T> function) {
        return doingSomething(u, function, "no");


    }
}
