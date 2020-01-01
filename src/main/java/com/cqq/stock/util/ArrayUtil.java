package com.cqq.stock.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayUtil {


    /**
     * 将数据进行分批
     *
     * @param list     list
     * @param pageSize 每批大小
     * @param <T>      泛型
     * @return 分批后的数据
     */
    public static <T> List<List<T>> batch(List<T> list, int pageSize) {
        if (list == null) {
            return null;
        }
        List<List<T>> l = new ArrayList<>();
        int pageNumber = (list.size() + pageSize - 1) / pageSize;
        for (int i = 0; i < pageNumber; i++) {
            l.add(list.stream().skip(i * pageSize).limit(pageSize).collect(Collectors.toList()));
        }
        return l;
    }
}
