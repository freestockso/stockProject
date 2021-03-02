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

public class KdjUtil2 {
    public <T extends KdjAble> void calculate(List<T> data) {
        Double lastK = null, lastD = null;

        List<Double> kList = new ArrayList<>();
        List<Double> dList = new ArrayList<>();
        List<Double> jList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            T row = data.get(i);
            if (lastK == null || Double.isNaN(lastK) || Double.isNaN(lastD) || lastD == null) {
                lastK = 50D;
                lastD = 50D;
            }
            double c = row.kdjClose();
            double l = row.kdjLow();
            double h = row.kdjHigh();
            double rsv = (c - l) / (h - l) * 100;
            double k = (2.0 / 3) * lastK + (1.0 / 3) * rsv;
            double d = (2.0 / 3) * lastD + (1.0 / 3) * k;
            double j = 3 * k - 2 * d;
            kList.add(k);
            dList.add(d);
            jList.add(j);

            lastK = k;
            lastD = d;
        }
        for (int i = 0; i < data.size(); i++) {
            T t = data.get(i);
            t.changeK(kList.get(i));
            t.changeD(dList.get(i));
            t.changeJ(jList.get(i));
        }
    }

    public static void main(String[] args) throws IOException {
        String s = FileUtil.readContent(new File("C:\\Users\\admin\\Desktop\\mt.json"));
        JSONObject jsonObject = JSON.parseObject(s);
        JSONObject data = jsonObject.getJSONObject("data");
        String string = data.getString("600519.SH");
        List<DailyResult> list = JSON.parseArray(string, DailyResult.class);
        new KdjUtil2().calculate(list);
        List<DailyResult> collect = list.stream().skip(600).collect(Collectors.toList());
        System.out.println(collect);


    }
}
