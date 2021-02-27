package com.cqq.stock.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cqq.stock.able.MACDAble;
import com.cqq.stock.entity.MacdStock;
import com.cqq.stock.entity.dto.DailyResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * macd计算工具
 * 可以计算股票的macd
 *
 * @author qiqi.chen
 */
public class MacdUtil {

    private int quicklyLine;

    private int slowLine;

    private int diffLine;

    public MacdUtil() {
        this.quicklyLine = 12;
        this.slowLine = 26;
        this.diffLine = 9;

    }

    /**
     * 计算股票的 MACD
     */
    public <T extends MACDAble> void calculateMACD(List<T> list) {
        if (list.size() < quicklyLine + diffLine || list.size() < slowLine + diffLine) {
            System.err.println("calculate MACD error, stock too less");
            return;
        }

        List<Double> emaForQuickly = getEMAForN(list, quicklyLine);
        List<Double> emaForSlow = getEMAForN(list, slowLine);
        List<Double> diff = getDiff(emaForQuickly, emaForSlow);
        List<Double> dea = getDea(diff, diffLine, quicklyLine, slowLine);
        getMacd(list, diff, dea, diffLine, quicklyLine, slowLine);


    }

    private <T extends MACDAble> void getMacd(List<T> list, List<Double> diff, List<Double> dea, int diffLine, int quicklyLine, int slowLine) {
        int m = Math.max(quicklyLine, slowLine);
        int n = diffLine;
        for (int i = n + m - 2; i < list.size(); i++) {
            T t = list.get(i);
            t.changeMacd((diff.get(i) - dea.get(i)) * 2);
        }
    }


    private List<Double> getDea(List<Double> diff, int n, int quicklyLine, int slowLine) {
        int m = Math.max(quicklyLine, slowLine);
        List<Double> dea = IntStream.range(0, n + m - 2).mapToObj(s -> (Double) null).collect(Collectors.toList());
        double k1 = 2.0 / (n + 1);
        double k2 = 1 - k1;
        Double firstValueAvg = IntStream.range(m - 1, n + m - 1).mapToObj(diff::get).reduce(0D, Double::sum) / n;
        dea.add(firstValueAvg);
        for (int i = n + m - 1; i < diff.size(); i++) {
            double v = k1 * diff.get(i) + k2 * dea.get(i - 1);
            dea.add(v);
        }
        return dea;
    }

    private List<Double> getDiff(List<Double> emaForQuickly, List<Double> emaForSlow) {
        List<Double> diff = new ArrayList<>();
        for (int i = 0; i < emaForQuickly.size(); i++) {
            Double slow = emaForSlow.get(i);
            Double quickly = emaForQuickly.get(i);
            if (slow != null && quickly != null) {
                double v = (quickly - slow) / 100.0;
                diff.add(v);
            } else {
                diff.add(null);
            }
        }
        return diff;
    }

    private <T extends MACDAble> List<Double> getEMAForN(List<T> list, int n) {
        List<Long> value = list.stream().map(MACDAble::close).collect(Collectors.toList());

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

        t2();
//        t1();

    }

    private static void t2() {

        File file = new File("C:\\Users\\admin\\Desktop\\mt.json");
        String content = FileUtil.readLines(file).stream().collect(Collectors.joining());
        JSONObject jsonObject = JSON.parseObject(content);
        JSONObject data = jsonObject.getJSONObject("data");
        String string = data.getString("600519.SH");
        List<DailyResult> list = JSON.parseArray(string, DailyResult.class);
        new MacdUtil().calculateMACD(list);
        List<DailyResult> ss = list.stream().skip(600).collect(Collectors.toList());


    }

    private static void t1() {
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
        new MacdUtil().calculateMACD(macdStockList);
    }
}

