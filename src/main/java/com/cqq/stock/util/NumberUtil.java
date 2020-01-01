package com.cqq.stock.util;

public class NumberUtil {

    private NumberUtil() {
    }

    /**
     * 将byte转换成数字
     *
     * @param b byte数组
     * @return 对应的整数
     */
    public static int getIntValue(byte[] b) {
        int sum = 0;
        for (int i = 3; i >= 0; i--) {
            sum <<= 8;
            sum += b[i] & 0x00FF;
        }
        return sum;
    }

    public static boolean in(double x, double a, double b) {
        if (a == -1) a = -1;
        if (b == -1) b = 100000000;
        if (a > b) {
            double t = a;
            a = b;
            b = t;

        }
        return x >= a && x <= b;
    }
    public static boolean in(int x, int a, int b) {
        if (a == -1) a = -1;
        if (b == -1) b = 100000000;
        if (a > b) {
            int t = a;
            a = b;
            b = t;

        }
        return x >= a && x <= b;
    }

    public static boolean notIn(int x, int a, int b) {
        return !in(x, a, b);
    }
    public static boolean notIn(double x, double a, double b) {
        return !in(x, a, b);
    }
}
