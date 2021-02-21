package com.cqq.stock.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BatchUtil {
    /**
     * 分批做某事
     *
     * @param list      总list
     * @param batchSize 每批大小
     * @param <T>       泛型
     */
    public static <T> Doing<T> with(List<T> list, int batchSize) {
        int pageSize = list.size() % batchSize == 0 ? (list.size() / batchSize) : (list.size() / batchSize) + 1;
        List<List<T>> ll = IntStream.range(0, pageSize)
                .mapToObj(currentPage -> list.stream().skip(currentPage * batchSize).limit(batchSize)
                        .collect(Collectors.toList())).collect(Collectors.toList());
        return new Doing<>(ll);
    }

    public static class Doing<T> {
        private List<List<T>> list;

        public Doing(List<List<T>> list) {
            this.list = list;
        }

        public void toDo(Consumer<List<T>> doing) {
            list.forEach(doing);
        }

        public void toDo(BiConsumer<Integer, List<T>> doing) {
            for (int i = 0; i < list.size(); i++) {
                doing.accept(i, list.get(i));
            }
        }

    }
}
