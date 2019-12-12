package com.cqq.stock.util;


import com.cqq.stock.entity.Buy;
import com.cqq.stock.entity.Sale;
import com.cqq.stock.entity.StockRecent;
import com.cqq.stock.interfaces.MyConsumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParseNetworkStockUtil {

    private static final String STR1 = "\"";
    private static final String HQ_STR_ = "hq_str_";
    static Map<Integer, MyConsumer> map;

    private static final String STR = "=\"";

    static {
        map = new HashMap<>(61);
        map.put(0, (stockRecent, value) -> {
            int equalPosition = value.lastIndexOf(STR);
            System.out.println(equalPosition);
            if (equalPosition != -1) {
                equalPosition += STR.length();
                String name = value.substring(equalPosition);
                stockRecent.setName(name);
                if (equalPosition >= 8) {
                    String code = value.substring(equalPosition - 8 - STR.length(), equalPosition - STR.length());
                    stockRecent.setCode(code);
                }
            }
        });
        map.put(1, StockRecent::setOpen);
        map.put(2, StockRecent::setClose);
        map.put(3, StockRecent::setNow);
        map.put(4, StockRecent::setHigh);
        map.put(5, StockRecent::setLow);
        map.put(6, StockRecent::setBuyPrice);
        map.put(7, StockRecent::setSalePrice);
        map.put(8, StockRecent::setNumberOfTransactions);
        map.put(9, StockRecent::setTransactionPrice);

        for (int i = 10; i < 20; i += 2) {
            map.put(i, (stockRecent, value) -> {
                Buy buy = new Buy();
                buy.setNumber(value);
                stockRecent.getBuyList().add(buy);
            });
            int finalI = i;
            map.put(i + 1, (stockRecent, value) -> stockRecent.getBuyList().get((finalI - 10) >> 1).setPrice(value));
        }

        for (int i = 20; i < 30; i += 2) {
            map.put(i, (stockRecent, value) -> {
                Sale sale = new Sale();
                sale.setNumber(value);
                stockRecent.getSaleList().add(sale);
            });
            int finalI = i;
            map.put(i + 1, (stockRecent, value) -> stockRecent.getSaleList().get((finalI - 20) >> 1).setPrice(value));
        }
        map.put(30, StockRecent::setDateTime);
        map.put(31, StockRecent::setTime);

    }

    static List<StockRecent> parse(String content) {
        String[] split = content.split(";");
        return Arrays.stream(split).map(ParseNetworkStockUtil::parseChild).collect(Collectors.toList());
    }

    /**
     * https://blog.csdn.net/fangquan1980/article/details/80006840
     *
     * @param content 解析前的单行内容
     * @return
     */
    private static StockRecent parseChild(String content) {
        StockRecent stockRecent = new StockRecent();
        if (content.contains(",")) {
            String code = content.substring(content.indexOf(" ") + 1, content.indexOf("="));
            code = code.substring(code.indexOf(HQ_STR_) + HQ_STR_.length());
            stockRecent.setCode(code);
        }
        String data = content.substring(content.indexOf(STR1), content.lastIndexOf(STR1));
        String[] splitData = data.split(",");
        stockRecent.setName(splitData[0]);
        stockRecent.setOpen(splitData[1]);
        stockRecent.setNow(splitData[3]);
        stockRecent.setClose(splitData[3]);//当时间在15:10以后成立
        stockRecent.setHigh(splitData[4]);
        stockRecent.setLow(splitData[5]);
        stockRecent.setBuyPrice(splitData[6]);
        stockRecent.setSalePrice(splitData[7]);
        stockRecent.setNumberOfTransactions(splitData[8]);
        stockRecent.setTransactionPrice(splitData[9]);
        stockRecent.setDateTime(splitData[30]);
        return stockRecent;
    }
}
