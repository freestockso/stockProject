package com.cqq.stock.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cqq.stock.able.KdjAble;
import com.cqq.stock.entity.dto.DailyResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KdjUtil3 {
    private int n;
    private int m1;
    private int m2;

    public KdjUtil3() {
        this.n = 9;
        this.m1 = 3;
        this.m2 = 3;

    }

    public <T extends KdjAble> void calculate(List<T> list) {
        List<Double> llu = llu(list, n);
        List<Double> hhu = hhu(list, n);
        List<Double> rsv = rsv(llu, hhu, list);
        List<Double> kList = sma(rsv, m1, 1);
        List<Double> dList = sma(kList, m2, 1);
        List<Double> jList = j(kList, dList, 3, 2);

        for (int i = 0; i < kList.size(); i++) {
            T t = list.get(i);
            t.changeK(kList.get(i));
            t.changeD(dList.get(i));
            t.changeJ(jList.get(i));

        }


    }

    public List<Double> j(List<Double> kList, List<Double> dList, int n, int m) {
        List<Double> jList = new ArrayList<>();
        for (int i = 0; i < kList.size(); i++) {
            Double k = kList.get(i);
            Double d = dList.get(i);
            if (k == null || d == null) {
                jList.add(null);
            } else {
                jList.add(k * n - d * m);
            }
        }
        return jList;

    }

    public List<Double> sma(List<Double> rsvList, int n, int m) {
        List<Double> smaList = new ArrayList<>();
        boolean start = false;
        for (int i = 0; i < rsvList.size(); i++) {
            Double rsv = rsvList.get(i);
            if (rsv == null) {
                smaList.add(50D);
            } else {
                if (!start) {
                    start = true;
                    smaList.add(rsv);
                } else {
                    Double s = (smaList.get(i - 1) * (n - m) + m * rsvList.get(i)) * 1.0 / n;
                    smaList.add(s);
                }
            }
        }
        return smaList;


    }

    private <T extends KdjAble> List<Double> rsv(List<Double> llu, List<Double> hhu, List<T> list) {

        List<Double> rsvList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            Double c = t.kdjClose();
            Double l = llu.get(i);
            Double h = hhu.get(i);
            if (c == null || l == null || h == null) {

                rsvList.add(null);

            } else {
                rsvList.add((c - l) / (h - l) * 100.0);
            }
        }
        return rsvList;
    }

    private <T extends KdjAble> List<Double> llu(List<T> list, int n) {
        List<Double> lowList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Double low = null;
            for (int j = i - n + 1; j <= i && j >= 0; j++) {
                T t = list.get(j);
                if (low == null) {
                    low = t.kdjLow();
                } else {
                    low = Math.min(t.kdjLow(), low);
                }
            }
            lowList.add(low);
        }
        return lowList;
    }

    private <T extends KdjAble> List<Double> hhu(List<T> list, int n) {
        List<Double> highList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Double high = null;
            for (int j = i - n + 1; j <= i && j >= 0; j++) {
                T t = list.get(j);
                if (high == null) {
                    high = t.kdjHigh();
                } else {
                    high = Math.max(t.kdjHigh(), high);
                }
            }
            highList.add(high);
        }
        return highList;
    }

    public static void main(String[] args) throws IOException {

        String s = FileUtil.readContent(new File("C:\\Users\\admin\\Desktop\\mt.json"));
        JSONObject jsonObject = JSON.parseObject(s);
        JSONObject data = jsonObject.getJSONObject("data");
        String string = data.getString("600519.SH");
        List<DailyResult> list = JSON.parseArray(string, DailyResult.class);
        new KdjUtil3().calculate(list);
        List<DailyResult> collect = list.stream().skip(600).collect(Collectors.toList());
        System.out.println(collect);
    }
}
