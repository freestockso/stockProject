package com.cqq.stock.util;

import com.cqq.stock.entity.CalculateStockTransactionInfo;

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
    public static long offset(long date, int basis) {
        int year = (int) (date / 10000);
        int day = (int) (date % 100);
        int month = (int) ((date / 100) % 100);
//        Date date1 = new Date(year,month,day);
        Calendar instance = Calendar.getInstance();
        instance.set(year, month - 1, day, 0, 0);
        long timeInMillis = instance.getTimeInMillis();
        timeInMillis -= basis * 24 * 60 * 60 * 1000L;
        Date d = new Date(timeInMillis);
        Calendar instance1 = Calendar.getInstance();
        instance1.setTime(d);
        int beforeYear = instance1.get(Calendar.YEAR);
        int beforeMonth = instance1.get(Calendar.MONTH) + 1;
        int beforeDate = instance1.get(Calendar.DATE);
        // 30/5*7 = 42  60
        return beforeYear * 10000 + beforeMonth * 100 + beforeDate;
    }
}
