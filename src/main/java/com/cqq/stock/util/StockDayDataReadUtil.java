package com.cqq.stock.util;

import com.cqq.stock.entity.po.StockDayData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * stockDayData读取工具
 */
public class StockDayDataReadUtil {

    public static List<StockDayData> readStockDayData(String filePath) {
        List<String> list = FileUtil.readLines(new File(filePath));
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        return list.stream().map(line -> {
            String[] split = line.split(" ");
            StockDayData stockDayData = new StockDayData();
            stockDayData.setCode(code(split[0]));
            stockDayData.setDate(Long.valueOf(split[1]));
            stockDayData.setOpen(Double.valueOf(split[2]));
            stockDayData.setHigh(Double.valueOf(split[3]));
            stockDayData.setLow(Double.valueOf(split[4]));
            stockDayData.setClose(Double.valueOf(split[5]));
            stockDayData.setChangeRate(Double.valueOf(split[7]));
            stockDayData.setVol(Double.valueOf(split[9]));
            stockDayData.setAmount(Double.valueOf(split[10]));

            return stockDayData;
        }).collect(Collectors.toList());

    }

    private static String code(String s) {
        String[] split = s.split("\\.");
        return split[1].toLowerCase() + split[0];
    }

}
