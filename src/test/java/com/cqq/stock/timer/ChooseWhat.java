package com.cqq.stock.timer;

import com.cqq.stock.entity.vo.RateData;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChooseWhat {

    @Test
    public void hello() throws IOException {
        String date = "20200106";
        File file = new File("D:\\newstock\\" + date + "\\result\\");
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        List<RateData> rateData = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(files[i]));
            List<String> list = new ArrayList<>();
            for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
                int pos = s.lastIndexOf("%");
                StringBuilder result = new StringBuilder();
                for (int z = pos - 1; z >= 0; z--) {
                    char c = s.charAt(z);
                    if (c >= '0' && c <= '9' || c == '.') {
                        result.insert(0, c);
                    } else {
                        break;
                    }

                }
                list.add(result.toString());

            }
            if (list.size() != 20) {
                System.out.println(files[i].getName() + " data error");
                continue;
            }
//            System.out.println(files[i].getName());
            double low = 0;
            double high = 0;
            for (int j = 0; j < 20; j++) {
                String x = list.get(j);
                double xValue = Double.parseDouble(x);
                if (j < 10) {

                    low += xValue;
                } else {
                    high += xValue;

                }
            }
            double rate = high / (low + high);
            RateData rateData1 = new RateData();
            rateData1.setCode(files[i].getName());
            rateData1.setHighRate(rate);
            rateData.add(rateData1);
        }
        rateData.sort(Comparator.comparing(RateData::getHighRate));
        rateData.stream().forEachOrdered(System.out::println);

    }
}
