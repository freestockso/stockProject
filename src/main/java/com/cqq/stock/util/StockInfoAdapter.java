package com.cqq.stock.util;

import com.cqq.stock.entity.StockRecent;
import com.cqq.stock.entity.StockTransactionInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.math.BigDecimal.ROUND_DOWN;

/**
 * 股票适配器
 * 将网络中请求到的实时股票数据转换成一个实体类对象
 */
public class StockInfoAdapter {

    private static final int MAX_LIST_CALL_SIZE = 800;

    public static StockTransactionInfo getStockTransactionInfoByStockRecent(StockRecent stockRecent) {
        StockTransactionInfo stockTransactionInfo = new StockTransactionInfo();
        String code = stockRecent.getCode();
        stockTransactionInfo.setCode(code);
        stockTransactionInfo.setOpen(NetworkPrice2DataPrice.getPrice(code,stockRecent.getOpen()));
        stockTransactionInfo.setClose(NetworkPrice2DataPrice.getPrice(code,stockRecent.getClose()));
        stockTransactionInfo.setHigh(NetworkPrice2DataPrice.getPrice(code,stockRecent.getHigh()));
        stockTransactionInfo.setLow(NetworkPrice2DataPrice.getPrice(code,stockRecent.getLow()));
//        stockTransactionInfo.setVol(double2Long(stockRecent.getNumberOfTransactions()));
//        stockTransactionInfo.setAmount(double2Long(stockRecent.getTransactionPrice()));
        stockTransactionInfo.setDate(date2Long(stockRecent.getDateTime()));
        return stockTransactionInfo;
    }

    private static Long date2Long(String dateTime) {
        if (dateTime == null) return null;
        return Long.valueOf(String.join("", dateTime.split("-")));
    }

    private static Long double2Long(String open) {
        if (open == null) return null;
        BigDecimal bigDecimal1 = new BigDecimal(open).multiply(new BigDecimal(1000)).setScale(0, ROUND_DOWN);
        return bigDecimal1.longValue();
    }

    private static final String TEMPLATE_URL = "http://hq.sinajs.cn/list=%s";

    public static List<StockTransactionInfo> getStockTransactionInfoByCodeList(List<String> codes) {

        List<StockTransactionInfo> all = new ArrayList<>();
        for (int i = 0; i < codes.size(); i += MAX_LIST_CALL_SIZE) {
            List<StockRecent> partOfData = getPartOfData(codes, i, MAX_LIST_CALL_SIZE);
            List<StockTransactionInfo> stockTransactionInfos = partOfData.stream()
                    .map(StockInfoAdapter::getStockTransactionInfoByStockRecent)
                    .collect(Collectors.toList());
            all.addAll(stockTransactionInfos);

        }
        return all;

    }

    public static List<StockRecent> getStockRecentByCodeList(List<String> codes) {

        List<StockRecent> all = new ArrayList<>();
        for (int i = 0; i < codes.size(); i += MAX_LIST_CALL_SIZE) {
            List<StockRecent> partOfData = getPartOfData(codes, i, MAX_LIST_CALL_SIZE);
            all.addAll(partOfData);

        }
        return all;

    }

    private static List<StockRecent> getPartOfData(List<String> codes, int beginPosition, int size) {
        List<String> list = codes.subList(beginPosition, min(beginPosition + size, codes.size()));
        String url = String.format(TEMPLATE_URL, String.join(",", list));
        MySpider mySpider = new MySpider();
        String content = mySpider.doing(url);
        return ParseNetworkStockUtil.parse(content);

    }

}
