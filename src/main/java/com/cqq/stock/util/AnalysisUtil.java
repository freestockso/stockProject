package com.cqq.stock.util;


import com.cqq.stock.entity.CalculateStockTransactionInfo;
import com.cqq.stock.entity.GoodPricePoint;
import com.cqq.stock.entity.StockTransactionInfo;

import java.util.List;
import java.util.stream.Collectors;


/**
 *  因为CCI不能直接作为买卖的依据，所以该分析程序已经废弃了
 */
@Deprecated
public class AnalysisUtil {
    public AnalysisUtil(int n) {
        this.N = n;
    }

    public AnalysisUtil() {

    }

    private int N = 14;


    public static int times = 0;

    /**
     * 给出一个股票最近n天的数据,返回对下一天的股市的合适买入价格和卖出价格
     *
     * @param stockCalculateList
     */
    public GoodPricePoint judgeMaxAndMinGoodPrice(List<CalculateStockTransactionInfo> stockCalculateList) {
//        System.out.println(times++);
        GoodPricePoint goodPricePoint = new GoodPricePoint();
        if (!stockCalculateList.isEmpty()) {
            goodPricePoint.setCode(stockCalculateList.get(0).getCode());
        }
        double minValue = getMinTp(stockCalculateList) * .6;
        double maxValue = getMaxTp(stockCalculateList) * 2.0;
        double split = (maxValue - minValue) / 1000;

        setSalePrice(goodPricePoint, stockCalculateList, minValue, maxValue, split);
        setBuyPrice(goodPricePoint, stockCalculateList, minValue, maxValue, split);
        setLastPrice(goodPricePoint, stockCalculateList);

//        calculateAvgGetMoney(goodPricePoint, stockCalculateList);

        CalculateStockTransactionInfo lastStock = stockCalculateList.get(stockCalculateList.size() - 1);
        goodPricePoint.setLastCci(lastStock.getCci());

//        goodPricePoint.setConsecutive60DaysOfAverageRise(judge60LineIsUp60(stockCalculateList));

//        calCalculateBreak(stockCalculateList, goodPricePoint);
        return goodPricePoint;
    }

    private void setLastPrice(GoodPricePoint goodPricePoint, List<CalculateStockTransactionInfo> stockCalculateList) {
        if (!stockCalculateList.isEmpty()) {
            goodPricePoint.setLastPrice(stockCalculateList.get(stockCalculateList.size() - 1).getTp());
        }
    }


/*    private int judge60LineIsUp60(List<StockCalculate> stockCalculateList) {
        int count = 0;
        if (stockCalculateList.size() > 60) {
            try {
                for (int i = stockCalculateList.size() - 1; i >= 60; i--) {
                    StockCalculate afterStock = stockCalculateList.get(i);
                    StockCalculate beforeStock = stockCalculateList.get(i - 1);
                    if (afterStock.getAverageTpIn60Day().compareTo(beforeStock.getAverageTpIn60Day()) < 0) {
                        break;
                    } else {
                        count++;
                    }

                }
            } catch (Exception e) {
                System.out.println("未知错误");
//                stockCalculateList.forEach(System.out::println);
//                System.out.println(stockCalculateList);
            }
        }
        return count;
    }*/


/*    private void setBuyPrice(GoodPricePoint goodPricePoint, List<StockCalculate> stockCalculateList, double minValue, double maxValue, double split) {
        for (double d = maxValue; d >= minValue; d -= split) {
            StockCalculate stockCalculate = getStockCalculate(stockCalculateList, d);
            if (stockCalculate.getCci() != null && stockCalculate.getCci().doubleValue() < -100) {
                goodPricePoint.setBuyPrice(d);
                break;
            }
        }
    }*/

    private void setBuyPrice(GoodPricePoint goodPricePoint, List<CalculateStockTransactionInfo> stockCalculateList, double minValue, double maxValue, double split) {
        if (stockCalculateList.size() < 2 * N) {
            goodPricePoint.setBuyPrice(-9999_9999L);
            return;
        }
        for (double d = maxValue; d >= minValue; d -= split) {
            CalculateStockTransactionInfo stockCalculate = getStockCalculate(stockCalculateList, d);
            if (stockCalculate.getCci() < -100) {
                goodPricePoint.setBuyPrice(d);
                break;
            }
        }
    }

    private void setSalePrice(GoodPricePoint goodPricePoint, List<CalculateStockTransactionInfo> stockCalculateList, double minValue, double maxValue, double split) {
        if (stockCalculateList.size() < 2 * N) {
            goodPricePoint.setSalePrice(9999_9999L);
            return;
        }
        for (double d = minValue; d < maxValue; d += split) {
            CalculateStockTransactionInfo stockCalculate = getStockCalculate(stockCalculateList, d);
            if (stockCalculate.getCci() > 100) {
                goodPricePoint.setSalePrice(d);
                break;
            }
        }
    }

    /**
     * 将 区间内的股票数据转换成 被计算过的股票数据
     *
     * @param stockList stock
     * @return
     */
    private List<CalculateStockTransactionInfo> getStockCalculates(List<StockTransactionInfo> stockList) {
        List<StockTransactionInfo> stockCalculateList = stockList.stream().map(CalculateStockTransactionInfo::new).collect(Collectors.toList());
        if (stockCalculateList.size() < 2 * N) {
            System.out.println("数据不足");
            return null;
        }
/*        for (int i = N; i < stockCalculateList.size(); i++) {
            StockCalculate stockCalculate = stockCalculateList.get(i);
            stockCalculate.ma(stockCalculateList.subList(i - N + 1, i + 1));
        }
        for (int i = N + N; i < stockCalculateList.size(); i++) {
            StockCalculate stockCalculate = stockCalculateList.get(i);
            stockCalculate.md(stockCalculateList.subList(i - N + 1, i + 1));
        }
        for (int i = N + N; i < stockCalculateList.size(); i++) {
            StockCalculate stockCalculate = stockCalculateList.get(i);
            stockCalculate.cci();
            stockCalculateList.set(i, stockCalculate);
        }*/
//        calculateAvgTp(stockCalculateList, 10, StockCalculate::setAverageTpIn10Day);
//        calculateAvgTp(stockCalculateList, 30, StockCalculate::setAverageTpIn30Day);
//        calculateAvgTp(stockCalculateList, 60, StockCalculate::setAverageTpIn60Day);
//        calculateAvgTp(stockCalculateList, 180, StockCalculate::setAverageTpIn180Day);
        return null;
    }

/*    private void calculateAvgTp(List<StockCalculate> stockCalculateList, int beginAvg, BiConsumer<StockCalculate, BigDecimal> setMethod) {

        for (int i = beginAvg; i < stockCalculateList.size(); i++) {
            Optional<BigDecimal> sum = stockCalculateList.subList(i - beginAvg, i + 1).stream().map(StockCalculate::getTp).reduce(BigDecimal::add);
            int finalI = i;
            sum.ifPresent(s -> setMethod.accept(stockCalculateList.get(finalI), s.divide(new BigDecimal(beginAvg), 4, BigDecimal.ROUND_CEILING)));
        }
    }*/

/*    private void calCalculateBreak(List<CalculateStockTransactionInfo> stockCalculateList, GoodPricePoint goodPricePoint) {
        boolean findLow = false;
        boolean isBegin = false;
        int v[] = new int[stockCalculateList.size()];
        for (int i = 0; i < stockCalculateList.size(); i++) {
            BigDecimal cci = stockCalculateList.get(i).getCci();
            if (cci == null) {
                continue;
            }
            double cciValue = cci.doubleValue();
            if (i == 0) {
                if (cciValue > 100) {
                    findLow = true;
                    v[0] = -1;
                    continue;
                } else if (cciValue < -100) {
                    v[0] = 0;
                    continue;
                }
            }
            if (notIn(cciValue, -100, 100)) {
                isBegin = true;
            }
            if (isBegin && findLow) {

            }
        }
    }*/

    private CalculateStockTransactionInfo getStockCalculate(List<CalculateStockTransactionInfo> stockCalculateList, double d) {
        StockTransactionInfo stock = new StockTransactionInfo();
        long i = (long) (d);
        stock.setOpen(i);
        stock.setClose(i);
        stock.setHigh(i);
        stock.setLow(i);
        CalculateStockTransactionInfo stockCalculate = new CalculateStockTransactionInfo(stock);
        CciUtil.tp(stockCalculate);
        CciUtil.ma(stockCalculateList.subList(stockCalculateList.size() - 1 - N + 1, stockCalculateList.size() - 1 + 1), stockCalculate);
        CciUtil.md(stockCalculateList.subList(stockCalculateList.size() - 1 - N + 1, stockCalculateList.size() - 1 + 1), stockCalculate);
        CciUtil.cci(stockCalculate);
        return stockCalculate;
    }

    private double getMaxTp(List<CalculateStockTransactionInfo> list) {
        return list.stream().map(CalculateStockTransactionInfo::getTp).reduce(Long::max).orElse(-1000000L);
    }

    private double getMinTp(List<CalculateStockTransactionInfo> list) {
        return list.stream().map(CalculateStockTransactionInfo::getTp).reduce(Long::min).orElse(-1000000L);
    }
}
