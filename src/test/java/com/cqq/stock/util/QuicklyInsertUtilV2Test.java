package com.cqq.stock.util;

import com.cqq.stock.entity.po.StockDayData;
import org.junit.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class QuicklyInsertUtilV2Test {

    /**
     * 3秒
     */
    @Test
    public void hello5() {

        TimingClock t2 = new TimingClock();
        TimingClock t = new TimingClock();
        File file = new File("D:\\data\\stock\\day");
        File[] files = file.listFiles();
        for (File value : files) {
            List<StockDayData> stockDayData = StockDayDataReadUtil.readStockDayData(value.getAbsolutePath());
            t.call("over");
        }
        t2.call("ok");


    }

    /**
     * 4612ms
     */
    @Test
    public void hello6() throws SQLException, ClassNotFoundException, InterruptedException {

        TimingClock t2 = new TimingClock();
        TimingClock t = new TimingClock();
        File file = new File("D:\\data\\stock\\day");
        File[] files = file.listFiles();
        Vector<StockDayData> list = new Vector<>();
        CountDownLatch countDownLatch = new CountDownLatch(files.length);
        for (File value : files) {
            new Thread(()->{

                List<StockDayData> stockDayData = StockDayDataReadUtil.readStockDayData(value.getAbsolutePath());
                list.addAll(stockDayData);
                countDownLatch.countDown();
            }).start();

        }
        countDownLatch.await();
        t2.call("read from desk ok");
        System.out.println(list.size());
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUsername("root");
        dataSourceProperties.setPassword("root");
        dataSourceProperties.setDriverClassName("com.mysql.jdbc.Driver");
        Map<String, List<StockDayData>> listMap = list.stream().collect(Collectors.groupingBy(StockDayData::getCode));
        t2.call("to map");
        listMap.forEach((k, v) -> {
            v.sort(Comparator.comparing(StockDayData::getDate));
            CciUtilV2.calculateAllCCI(v);
        });
        t2.call("sort and calculate");
        dataSourceProperties.setUrl("jdbc:mysql://localhost:3306/stock_project?rewriteBatchedStatements=true&serverTimezone=UTC");
        QuicklyInsertUtilV2.quicklySaveToDatabase(list, 50_0000, dataSourceProperties);
        t2.call("insert ok ");


    }

}
