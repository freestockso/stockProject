package com.cqq.stock.util;

import com.cqq.stock.entity.Stock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static com.cqq.stock.constants.FileConstant.DAY_SH_DATA_FILE;

/**
 * 快速读取工具
 */
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
    public static List<Integer> readInStartTimeAndEndTime(File file, int startTime, int endTime) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        if (startTime > endTime) {
            return new ArrayList<>();
        }
        //总字节数
        long length = randomAccessFile.length();
        byte b[] = new byte[4];
        randomAccessFile.read(b);


        return null;
    }

    public static Stock readLastStock(File file) throws IOException {
        String name = file.getName();
        String code = name.substring(0,name.indexOf('.'));
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
        long length = randomAccessFile.length();
        long readPosition = length - STOCK_UNIT;
        randomAccessFile.seek(readPosition);
        byte b[] = new byte[4];
        List<Integer>values = new ArrayList<>();
        for(int i = 0 ; i < 8 ;i++){
            randomAccessFile.read(b);
            int value = NumberUtil.getIntValue(b);
            values.add(value);

        }
        List<Stock> stocks = ReadUtil.getStocks(values,code);
        return stocks.get(0);


    }


    public static void main(String... args) throws IOException {
        readInStartTimeAndEndTime(new File(""), 2019_02_01, 2019_03_01);

    }


}
