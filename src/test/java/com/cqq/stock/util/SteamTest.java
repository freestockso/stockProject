package com.cqq.stock.util;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * 这是Stream流的一个测试
 */
public class SteamTest {
    public static void main(String[] args) {
        //100w数据在并行流下相加差不多700-800毫秒
//        long time = getTime();

        //在没有并行流的情况下运行 差不多 11000毫秒
        long time2 = getTimeNot();

    }

    private static long  getTime() {
        long time = System.currentTimeMillis();
        long sum = LongStream.range(0, 100_0000_0000L).parallel().sum();
        long time2 = System.currentTimeMillis();
        System.out.println("spend time: "+ +(time2 - time));
        System.out.println(String.format("result: %d", sum));
        return time2 - time;
    }
    private static long  getTimeNot() {
        long time = System.currentTimeMillis();
        long sum = LongStream.range(0, 100_0000_0000L).sum();
        long time2 = System.currentTimeMillis();
        System.out.println("spend time: "+ +(time2 - time));
        System.out.println(String.format("result: %d", sum));
        return time2 - time;
    }
}
// 6min 十倍 300 30秒
