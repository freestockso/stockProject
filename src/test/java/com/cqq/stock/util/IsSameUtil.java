package com.cqq.stock.util;

import com.cqq.stock.constants.FileConstant;
import com.cqq.stock.entity.Stock;
import com.cqq.stock.entity.StockRecent;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.cqq.stock.util.StockInfoAdapter.getStockRecentByCodeList;

/**
 * 判断从文件中获取的数据与网络中同步的数据是否一致
 * 利用该类编写了 NetworkPrice2DatePrice 公式
 */
public class IsSameUtil {

    @Test
    public void hellO() throws IOException {
        long time1 = System.currentTimeMillis();
//        File dir1 = new File(FileConstant.DAY_SH_DATA_FILE);
        File dir = new File(FileConstant.DAY_SH_DATA_FILE);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        List<Stock> dataFileStock = new ArrayList<>();
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Stock stock = QuicklyReadUtil.readLastStock(files[i]);
            String code = files[i].getName().substring(0, files[i].getName().indexOf('.'));
//            System.out.println(code);
            codes.add(code);
            dataFileStock.add(stock);
        }
        List<StockRecent> networkStock = getStockRecentByCodeList(codes);
        networkStock.removeIf(Objects::isNull);

        System.out.println("stockRecentByCodeList:" + networkStock.size());
        System.out.println("dataFileStock:" + dataFileStock.size());
        Map<String, List<Stock>> dataFileStockMap = dataFileStock.stream().collect(Collectors.groupingBy(Stock::getCode));
        Map<String, List<StockRecent>> networkStockMap = networkStock.stream().collect(Collectors.groupingBy(StockRecent::getCode));
        Set<String> keys = networkStockMap.keySet();
//        int k = 0;
        BufferedWriter lowTxt = new BufferedWriter(new FileWriter(new File("C:\\Users\\Administrator\\Desktop\\low.txt")));
        BufferedWriter highTxt = new BufferedWriter(new FileWriter(new File("C:\\Users\\Administrator\\Desktop\\high.txt")));
        int errorCount = 0;
        for (String key : keys) {
            StockRecent stockRecent = networkStockMap.get(key).get(0);
            Stock stock = dataFileStockMap.get(key).get(0);

            int val = StockJudgeUtil.isLow(key) ? 1000 : 1000;
            BigDecimal networkValue = new BigDecimal(stockRecent.getClose()).multiply(new BigDecimal(val)).setScale(0, RoundingMode.HALF_DOWN);
            if (!StockJudgeUtil.isLow(key)) {
                networkValue = networkValue.divide(new BigDecimal(10), 0, RoundingMode.HALF_UP);
            }
            long networkLongValue = networkValue.longValue();
            if (networkLongValue != stock.getClose() && networkLongValue != 0) {
                System.out.println("code:" + key + " dataFileClose:" + stock.getClose() + " NetworkClose:" + stockRecent.getClose() + " v:" + networkLongValue);
                errorCount++;

                lowTxt.write(key + "\r\n");
            } else {
                highTxt.write(key + "\r\n");

            }
        }
        lowTxt.close();
        highTxt.close();
        System.out.println("errorCount:" + errorCount);
        System.out.println("key.size:" + keys.size());


        long time2 = System.currentTimeMillis();
        System.out.println(time2 - time1);
    }

    @Test
    public void solveBug() {
//        List<StockRecent> sh001 = getStockRecentByCodeList(Collections.singletonList("sh001"));
//        System.out.println(sh001);
    }


}
