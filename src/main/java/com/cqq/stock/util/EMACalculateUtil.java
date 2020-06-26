package com.cqq.stock.util;

import com.cqq.stock.entity.EMAStock;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Deprecated
public class EMACalculateUtil {


    /**
     * 计算股票的EMA 指数滑动平均数
     *
     * @param list 近期运行的股票
     * @return
     */
/*
    public static List<EMAStock> calculateEMA(List<StockTransactionInfo> list) {
        List<EMAStock> emaStockList = list.stream().map(EMAStock::new).collect(Collectors.toList());
        //平滑参数
        int SMOOTHING = 2;
        int nDay = list.size();
        double k = SMOOTHING * 1.0 / (nDay + 1);
        double _1_k = 1 - (SMOOTHING * 1.0 / (nDay + 1));
        for (int i = 0; i < emaStockList.size(); i++) {
            if (i == 0) {
                emaStockList.get(i).putEmaValue(nDay, emaStockList.get(i).getClose());
            } else {
                EMAStock yesterday = emaStockList.get(i - 1);
                EMAStock today = emaStockList.get(i);
                Long todayClose = today.getClose();
                double emaYesterday = yesterday.getEmaValue(nDay);
                double todayEMA = todayClose * k + emaYesterday * _1_k;
                today.putEmaValue(nDay, todayEMA);
            }
        }
        return emaStockList;
    }
*/

    /**
     * 计算股票的 macd
     * @param list list
     */
    public static void calculateAll(List<EMAStock> list) {
        calculateAll(list, 12, 26, 9);

    }

    /**
     * 计算股票的DIF
     *
     * @param list
     * @param shortDay 默认为12
     * @param longDay  默认为26
     * @param n        默认为9
     */
    public static void calculateAll(List<EMAStock> list, int shortDay, int longDay, int n) {
        double k = 2.0 / n;
        calEmaOfNday(list, shortDay);
        calEmaOfNday(list, longDay);
        calculateDif(list, shortDay, longDay);
        calculateDEA(list, shortDay, longDay, n);
        calculateMacd(list, shortDay, longDay, n);
        int value = calculateGodXAndDiedX(list, shortDay, longDay);
        if (value >= 2) {
            System.out.println(String.format("股票%s在近期出现2次及以上金叉", list.get(0).getCode()));
        }
    }

    private static int calculateGodXAndDiedX(List<EMAStock> list, int shortDay, int longDay) {
        int value = 0;
        List<Long> values = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            EMAStock yesterdayStock = list.get(i - 1);
            EMAStock todayStock = list.get(i);
            double dif1 = yesterdayStock.getDifValue(shortDay, longDay);
            double dea1 = yesterdayStock.getDeaValue(shortDay, longDay);
            double dif2 = todayStock.getDifValue(shortDay, longDay);
            double dea2 = todayStock.getDeaValue(shortDay, longDay);
            if (dif1 <= dea1 && dif2 > dea2 && dif2 > dif1) {
//                System.out.println(String.format("股票%s在%d出现金叉", yesterdayStock.getCode(), yesterdayStock.getDate()));
                values.add(yesterdayStock.getDate());
                if (yesterdayStock.getDate() > 2019_10_13) {
                    value++;
                }
            }
            if (dif1 >= dea1 && dif2 < dea2 && dif2 < dif1) {
//                System.out.println(String.format("股票%s在%d出现死叉", yesterdayStock.getCode(), yesterdayStock.getDate()));
                values.add(-yesterdayStock.getDate());
            }
        }
        return value;
    }

    private static void calculateMacd(List<EMAStock> list, int shortDay, int longDay, int n) {
        for (int i = 0; i < list.size(); i++) {
            EMAStock today = list.get(i);
            double v = today.getDifValue(shortDay, longDay) - today.getDeaValue(shortDay, longDay);
            today.setMacd(v * 2);
        }
    }

    private static void calculateDEA(List<EMAStock> list, int shortDay, int longDay, int n) {
        double k = 2.0 / (n + 1);
        if (list.isEmpty()) {
            return;
        }
        list.get(0).putDeaValue(shortDay, longDay, list.get(0).getDifValue(shortDay, longDay));
        for (int i = 1; i < list.size(); i++) {
            EMAStock yesterday = list.get(i - 1);
            EMAStock today = list.get(i);
            Double yesterdayDeaValue = yesterday.getDeaValue(shortDay, longDay);
            Double todayDEeaValue = today.getDifValue(shortDay, longDay) * k + yesterdayDeaValue * (1 - k);
            today.putDeaValue(shortDay, longDay, todayDEeaValue);
        }
    }

    private static void calculateDif(List<EMAStock> list, int shortDay, int longDay) {
        for (int i = 0; i < list.size(); i++) {
            EMAStock today = list.get(i);
            double dif = today.getEmaValue(shortDay) - today.getEmaValue(longDay);
            today.putDifValue(shortDay, longDay, dif);
        }
    }

    private static void calEmaOfNday(List<EMAStock> list, int n) {
        double k = 2.0 / (1 + n);
        if (list.isEmpty()) {
            return;
        }
        list.get(0).putEmaValue(n, list.get(0).getClose());
        for (int i = 1; i < list.size(); i++) {
            EMAStock yesterday = list.get(i - 1);
            EMAStock today = list.get(i);
            double todayEMA = (today.getClose() / 100.0) * k + yesterday.getEmaValue(n) * (1 - k);
            list.get(i).putEmaValue(n, todayEMA);
        }
    }

}
