package com.cqq.stock.util;

import com.cqq.stock.entity.Stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cqq.stock.constants.FileConstant.DAY_SH_DATA_FILE;
import static com.cqq.stock.constants.FileConstant.DAY_SZ_DATA_FILE;
import static com.cqq.stock.util.NumberUtil.getIntValue;

/**
 * 读取本地的股票数据文件
 * 通信达数据文件
 */
public class ReadUtil {

    private ReadUtil() {

    }


    private static long count = 0;
    private static long times = 0;

    private static List<Stock> parseIntToStockAdditionalCode(File file, String code) {
        long time = System.currentTimeMillis();
        List<Stock> collect = parseIntegerListToStockList(file).stream()
                .peek(stock -> stock.setCode(code))
                .collect(Collectors.toList());
        count++;
        times += System.currentTimeMillis() - time;
        System.out.println(count + " " + times);
        return collect;
    }


    private static Map<Integer, BiConsumer<Stock, Long>> thingMap = getThingMap();

    /**
     * 解析 单个 file, 生成 stock 数组
     *
     * @param file 就像 sh000001.sh
     * @return stock数组
     */
    private static List<Stock> parseIntegerListToStockList(File file) {
        List<Integer> values = getIntegerListByFile(file);
        return getStocks(values);
    }

    static List<Stock> getStocks(List<Integer> values,String code) {
        List<Stock> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i += 8) {
            Stock stock = new Stock();
            stock.setCode(code);
            setValue(values, list, i, stock);
        }
        return list;
    }

    private static void setValue(List<Integer> values, List<Stock> list, int i, Stock stock) {
        for (int j = 0; j < 8; j++) {
            int k = i + j;
            Integer integer = values.get(k);
            thingMap.get(k % 8).accept(stock, integer.longValue());
            if (k % 8 == 7) {
                list.add(stock);
            }
        }
    }

    static List<Stock> getStocks(List<Integer> values) {
        List<Stock> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i += 8) {
            Stock stock = new Stock();
            setValue(values, list, i, stock);
        }
        return list;
    }

    private static Map<Integer, BiConsumer<Stock, Long>> getThingMap() {
        return new HashMap<Integer, BiConsumer<Stock, Long>>() {{
            put(0, Stock::setDate);
            put(1, Stock::setOpen);
            put(2, Stock::setHigh);
            put(3, Stock::setLow);
            put(4, Stock::setClose);
            put(5, Stock::setAmount);
            put(6, Stock::setVol);
            put(7, Stock::setReserv);
        }};
    }


    /**
     * 从本地磁盘读取股票文件
     *
     * @param stockPredicate 股票筛选条加
     * @param skip           跳过前几个数
     * @param limit          读取文件限制数
     * @param filePredicate  文件规则限制 文件名类似 sh000001.day
     * @return R
     */
    public static Map<String, List<Stock>> readStockList(int skip, int limit, Predicate<Stock> stockPredicate, Predicate<File> filePredicate) {
        Map<String, List<Stock>> stockMap = new HashMap<>();
        Stream.of(
                new File(DAY_SH_DATA_FILE),
                new File(DAY_SZ_DATA_FILE)
        ).map(root -> {
            File[] list = root.listFiles();
            if (list == null) return new ArrayList<File>();
            return Arrays.stream(list).filter(filePredicate).collect(Collectors.toList());
        }).reduce(new ArrayList<>(), (left, right) -> {
            left.addAll(right);
            return left;
        }).stream().skip(skip).limit(limit).map(file -> {
            List<Stock> readStock = parseIntToStockAdditionalCode(file, file.getName().substring(0, file.getName().indexOf('.')));
            return readStock.stream().filter(stockPredicate).collect(Collectors.toList());

        }).forEach(ls -> {
            String key = ls.stream().findFirst().map(Stock::getCode).orElse(System.currentTimeMillis() + "");
            stockMap.put(key, ls);
        });
        return stockMap;
    }


    /**
     * 从文件中读取所有数字
     *
     * @param file 文件
     * @return int list
     */
    private static List<Integer> getIntegerListByFile(File file) {
        List<Integer> values = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(file);
            byte[] b = new byte[4];
            for (int read = inputStream.read(b); read != -1; read = inputStream.read(b)) {
                int sum = getIntValue(b);
                values.add(sum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }
}

