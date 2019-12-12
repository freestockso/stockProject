package com.cqq.stock.util;

import com.cqq.stock.entity.CalculateStockTransactionInfo;
import com.cqq.stock.entity.StockTransactionInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 经典的CCI计算工具类
 */
public class CciUtil {
    private static final int N = 14;

    /**
     * 传入某只股票的信息 2N 以上的 信息， 返回被CCI计算过的结果
     *
     * @param stockTransactionInfos 2N日的信息
     * @return 计算过结果的信息
     */
    public static List<CalculateStockTransactionInfo> main(List<StockTransactionInfo> stockTransactionInfos) {
        List<CalculateStockTransactionInfo> stockList = stockTransactionInfos.stream()
                .map(CalculateStockTransactionInfo::new)
                .peek(CciUtil::tp).collect(Collectors.toList());

        for (int i = N; i < stockList.size(); i++) {
            CalculateStockTransactionInfo calculateStockTransactionInfo = stockList.get(i);
            CciUtil.ma(stockList.subList(i - N + 1, i + 1), calculateStockTransactionInfo);
        }
        for (int i = N + N; i < stockList.size(); i++) {
            CalculateStockTransactionInfo calculateStockTransactionInfo = stockList.get(i);
            CciUtil.md(stockList.subList(i - N + 1, i + 1), calculateStockTransactionInfo);
        }
        for (int i = N + N; i < stockList.size(); i++) {
            CalculateStockTransactionInfo calculateStockTransactionInfo = stockList.get(i);
            CciUtil.cci(calculateStockTransactionInfo);
        }
        return stockList;

    }

    /**
     * 传入某只股票的信息 2N 以上的 信息， 返回被CCI计算过的结果，并且是以原类型对象返回的
     *
     * @param stockTransactionInfos 2N日的信息
     * @return 计算过结果的信息
     */
    public static List<StockTransactionInfo> mainAndOrigin(List<StockTransactionInfo> stockTransactionInfos) {
        List<CalculateStockTransactionInfo> main = main(stockTransactionInfos);
        for (int i = 0; i < stockTransactionInfos.size(); i++) {
            stockTransactionInfos.get(i).setCci(main.get(i).getCci());
        }
        return stockTransactionInfos;

    }

    /**
     * 返回这只股票 最高价+最低价+收盘价的平均数
     */
    public static void tp(CalculateStockTransactionInfo calculateStockTransactionInfo) {
        calculateStockTransactionInfo.setTp((
                calculateStockTransactionInfo.getHigh()
                        + calculateStockTransactionInfo.getLow()
                        + calculateStockTransactionInfo.getClose()
        ) / 3);
    }

    /**
     * @param list 最近n日的股票
     *             最近n日的股票Pt平均值
     */
    public static void ma(List<CalculateStockTransactionInfo> list, CalculateStockTransactionInfo calculateStockTransactionInfo) {
        Optional<Long> sum = list.stream().map(CalculateStockTransactionInfo::getTp).reduce(Long::sum);
        calculateStockTransactionInfo.setMa(sum.map(val -> val / list.size()).orElse(0L));
    }

    /**
     * @param list 最近n天的股票
     *             最近n日 (ma-Pt)平均值
     */
    public static void md(List<CalculateStockTransactionInfo> list, CalculateStockTransactionInfo calculateStockTransactionInfo) {
        Optional<Long> sum = list.stream()
                .map(CalculateStockTransactionInfo::getTp)
                .map(val -> Math.abs(val - calculateStockTransactionInfo.getMa()))
                .reduce(Long::sum);
        calculateStockTransactionInfo.setMd(sum.map(s -> s / list.size()).orElse(0L));
    }

    /**
     * 计算该股票的CCI
     */
    public static void cci(CalculateStockTransactionInfo s) {
        try {
            s.setCci(((s.getTp() - s.getMa()) / 1.0 / s.getMd()) / 0.015);
        } catch (Exception e) {
            s.setCci(Integer.MAX_VALUE);
        }
    }
}
