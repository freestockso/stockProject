package com.cqq.stock.util;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class StockResultUtil {

    public static List<Double> getValueList(File file) {
        List<String> list = FileUtil.readLines(file);
        return list.stream().map(s -> {
            String all = "";
            boolean start = false;
            for (int i = s.length() - 1; i >= 0; i--) {
                char c = s.charAt(i);
                if (start && !(c == '.' || (c >= '0' && c <= '9'))) {
                    break;

                }
                if (c == '.' || (c >= '0' && c <= '9')) {
                    start = true;
                    all = c + all;
                }

            }
            return all;
        }).map(Double::valueOf).collect(Collectors.toList());
    }
}
