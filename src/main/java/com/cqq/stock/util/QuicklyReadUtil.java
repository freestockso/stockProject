package com.cqq.stock.util;

import com.cqq.stock.constants.FileConstant;
import com.cqq.stock.entity.Stock;
import com.cqq.stock.entity.StockTransactionInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cqq.stock.constants.FileConstant.DAY_SH_DATA_FILE;

/**
 * 快速读取工具
 */
@Slf4j
public class QuicklyReadUtil {

    public static final int STOCK_UNIT = 32;

    public static void demo() throws IOException {
        File dir = new File(DAY_SH_DATA_FILE);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        long time1 = System.currentTimeMillis();
        int errorCount = 0;
        for (int i = 0; i < 1; i++) {
            File file = files[i];
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            long length = randomAccessFile.length();
            System.out.println("length:" + length);
            length = randomAccessFile.length();
            System.out.println("length:" + length);
            randomAccessFile.seek(length - STOCK_UNIT);
            byte[] b = new byte[4];
            randomAccessFile.read(b);
            int intValue = NumberUtil.getIntValue(b);
            System.out.println(intValue);

        }
        long time2 = System.currentTimeMillis();
        System.out.println("error count:" + errorCount);
        System.out.println("spend time:" + (time2 - time1));
    }

    /**
     * @param startTime 2019_01_02
     * @param endTime   2019_03_02
     */
    public static List<Integer> readInStartTimeAndEndTime(File file, long startTime, long endTime) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        if (startTime > endTime) {
            return new ArrayList<>();
        }
        List<Integer> list = new ArrayList<>();
        //总股票数
        long length = randomAccessFile.length() / 32;

        byte[] b = new byte[4];
        for (int i = 0; i < length; i++) {
            randomAccessFile.seek(i * 32);
            randomAccessFile.read(b);
            int intValue = NumberUtil.getIntValue(b);
            list.add(intValue);
        }
        int startIndex = -1, endIndex = -1;
        if (startTime > list.get(list.size() - 1)) {
            return new ArrayList<>();
        }
        if (endTime < list.get(0)) {
            return new ArrayList<>();
        }
        if (startTime < list.get(0)) {
            startIndex = 0;
        }
        if (endTime > list.get(list.size() - 1)) {
            endIndex = list.size() - 1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (startTime <= list.get(i)) {
                startIndex = i;
                break;

            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (endTime >= list.get(i)) {
                endIndex = i;
                break;
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int i = startIndex * 32; i <= endIndex * 32; i += 32) {
            for (int j = 0; j < 32; j += 4) {
                try {
                    randomAccessFile.seek(i + j);
                    randomAccessFile.read(b);
                    int intValue = NumberUtil.getIntValue(b);
                    result.add(intValue);
                } catch (IOException e) {
                    System.out.println("startIndex:" + startIndex);
                    System.out.println("endIndex:" + endIndex);
                    e.printStackTrace();
                }
            }
//            System.out.println(intValue);
        }
        return result;
    }

    public static Stock readLastStock(File file) throws IOException {
        String name = file.getName();
        String code = name.substring(0, name.indexOf('.'));
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        long length = randomAccessFile.length();
        long readPosition = length - STOCK_UNIT;
        randomAccessFile.seek(readPosition);
        byte b[] = new byte[4];
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            randomAccessFile.read(b);
            int value = NumberUtil.getIntValue(b);
            values.add(value);

        }
        List<Stock> stocks = ReadUtil.getStocks(values, code);
        return stocks.get(0);


    }

    /**
     * 避免了ReadUtil的一些缺点，包括全局加载后的条件赛选
     * 在小范围下收集股票，非常节约时间
     * 从file中根据区间取出股票数据进行整合
     *
     * @param file      要进行获取的股票文件
     * @param startTime 开始时间 2019_01_02
     * @param endTime   结束时间 2019_02_02
     * @return list
     * @throws IOException
     */
    public static List<StockTransactionInfo> readStockBySection(File file, long startTime, long endTime) {
        long time1 = System.currentTimeMillis();

        List<Integer> list = null;
        try {
            list = readInStartTimeAndEndTime(file, startTime, endTime);
            String name = file.getName();
            String code = name.substring(0, name.indexOf('.'));
            List<StockTransactionInfo> stocksByGeneric = ReadUtil.getStocksByGeneric(list, code, StockTransactionInfo.class);
            long time2 = System.currentTimeMillis();
            System.out.println(time2 - time1);
            return  stocksByGeneric;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    /**
     * 查询股票数据文件在startTime-endTime区间内的所有数据
     * 它节省ReadUtil大量无意义的时间,它非常的酷
     *
     * @param startTime 2012_03_04
     * @param endTime   2012_03_05
     * @return map
     * 经过测试2个月的时间，也只需要34s
     * 2天的数据 只需要29s
     */
    public static Map<String, List<StockTransactionInfo>> stockMap(long startTime, long endTime) {
        return Stream.of(DAY_SH_DATA_FILE, FileConstant.DAY_SZ_DATA_FILE)
                .map(File::new)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .map(array -> Arrays.stream(array).collect(Collectors.toList()))
                .reduce(new ArrayList<>(), (l, r) -> {
                    l.addAll(r);
                    return l;
                }).stream()
                .map(file -> readStockBySection(file, startTime, endTime))
                .reduce(new ArrayList<>(), (l, r) -> {
                    l.addAll(r);
                    return l;
                }).stream()
                .collect(Collectors.groupingBy(StockTransactionInfo::getCode));
    }

    public static void main(String... args) {
        long time1 = System.currentTimeMillis();
        Map<String, List<StockTransactionInfo>> stringListMap = stockMap(2020_01_01, 2020_01_05);
        long time2 = System.currentTimeMillis();
        System.out.println(time2 - time1);
/*
        stringListMap.forEach((k, v) -> {
            System.out.println(v);
        });
*/
    }


}
