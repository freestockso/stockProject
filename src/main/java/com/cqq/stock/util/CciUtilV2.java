package com.cqq.stock.util;

import com.cqq.stock.able.CCIAble;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CciUtilV2 {
    public static final int N = 14;

    /**
     * 计算股票的CCI
     * 默认传入的股票为相同code的,且已经是按date排序好的
     *
     * @param stock 2N日的信息
     */
    public static <T extends CCIAble> void calculateAllCCI(List<T> stock) {


        List<Double> prices = stock.stream()
                .sorted(Comparator.comparing(CCIAble::getDate))
                .map(CCIAble::getPrice).collect(Collectors.toList());

        //计算MA
        List<Double> listForMA = getListForMA(prices);
        //计算MD
        List<Double> listForMD = getListForMD(listForMA, prices);
        //计算CCI
        List<Double> listForCCI = getListForCCI(prices, listForMA, listForMD);

        for (int i = 0; i < stock.size(); i++) {
            stock.get(i).setCci(listForCCI.get(i));
        }


    }

    private static List<Double> getListForCCI(List<Double> prices, List<Double> listForMA, List<Double> listForMD) {
        List<Double> listForCCI = IntStream.range(0, N - 1).mapToObj(i -> (Double) null).collect(Collectors.toList());
        for (int i = N - 1; i < prices.size(); i++) {
            double z = (prices.get(i) - listForMA.get(i)) / (listForMD.get(i) * 0.015);
            listForCCI.add(z);
        }
        return listForCCI;
    }

    private static List<Double> getListForMD(List<Double> listForMA, List<Double> prices) {
        List<Double> mdList = IntStream.range(0, N - 1).mapToObj(i -> (Double) null).collect(Collectors.toList());
        for (int i = N - 1; i < prices.size(); i++) {
            double s = 0D;
            for (int j = 0; j < N; j++) {
                s += Math.abs(listForMA.get(i) - prices.get(i - j));
            }
            mdList.add(s / N);
        }
        return mdList;
    }

    /**
     * 根据price计算ma数组
     *
     * @param prices price
     * @return maList
     */
    private static List<Double> getListForMA(List<Double> prices) {
        List<Double> ma = IntStream.range(0, N - 1).mapToObj(i -> (Double) null).collect(Collectors.toList());
        double firstMA = prices.stream().skip(0).limit(N).mapToDouble(s -> s).summaryStatistics().getAverage();
        ma.add(firstMA);
        double k = firstMA;
        for (int i = N; i < prices.size(); i++) {
            k += (prices.get(i) - prices.get(i - N)) / N;
            ma.add(k);
        }
        return ma;
    }

}
