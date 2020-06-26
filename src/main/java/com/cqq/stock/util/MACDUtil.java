package com.cqq.stock.util;

import com.cqq.stock.able.MACDAble;
import com.cqq.stock.entity.MacdStock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 该工具还没有开发好
 */
public class MACDUtil {


    /**
     * 计算股票的 MACD
     */
    public static <T extends MACDAble> void calculateMACD(List<T> list) {
        int n = 12;
        int m = 26;
        if (list.size() < n || list.size() < m) {
            System.err.println("calculate MACD error, stock too less");
            return;
        }

        List<Double> emaForN = getEMAForN(list, n);
        List<Double> emaForM = getEMAForN(list, m);


    }

    private static <T extends MACDAble> List<Double> getEMAForN(List<T> list, int n) {
        List<Long> value = list.stream().map(MACDAble::getClose).collect(Collectors.toList());

        List<Double> emaForN = IntStream.range(0, n - 1).mapToObj(s -> (Double) null).collect(Collectors.toList());

        double k1 = 2.0 / (n + 1);
        double k2 = 1 - k1;
        Double firstValueAvg = IntStream.range(0, n).mapToObj(value::get).map(Double::valueOf).reduce(0D, Double::sum) / n;
        emaForN.add(firstValueAvg);
        for (int i = n; i < list.size(); i++) {
            double v = k1 * value.get(i) + k2 * emaForN.get(i - 1);
            emaForN.add(v);
        }
        return emaForN;
    }

    public static void main(String[] args) {

        double[] d = new double[]{
                459.99,
                448.85, 446.06, 450.81, 442.8, 448.97, 444.57, 441.4, 430.47, 420.05, 431.14,
                425.66, 430.58, 431.72, 437.87, 428.43, 428.35, 432.5, 443.66, 455.72, 454.49,
                452.08, 452.73, 461.91, 463.58, 461.14, 452.08, 442.66, 428.91, 429.79, 431.99,
                427.72, 423.2, 426.21, 426.98, 435.69, 434.33, 429.8, 419.85, 426.24, 402.8,
                392.05, 390.53, 398.67, 406.13, 405.46, 408.38, 417.2, 430.12, 442.78, 439.29,
                445.52, 449.98, 460.71, 458.66, 463.84, 456.77, 452.97, 454.74, 443.86, 428.85,
                434.58, 433.26, 442.93, 439.66, 441.35
        };
        List<MacdStock> macdStockList = Arrays.stream(d).mapToObj(s -> {
            MacdStock macdStock = new MacdStock();
            macdStock.setClose((long) (s * 100));
            return macdStock;
        }).collect(Collectors.toList());
        calculateMACD(macdStockList);

    }
}

