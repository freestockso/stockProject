package com.cqq.stock.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.stock.interfaces.PageAble;

import java.util.List;
import java.util.stream.Collectors;

public class PageUtil {
    public static <T> Page<T> toPage(PageAble condition) {
        return new Page<>(condition.getCurrent(), condition.getLimit());
    }


    public static <T> Page<T> page(List<T> list, Long current, Long size) {
        List<T> pageList = list.stream().skip((current - 1) * size).limit(size).collect(Collectors.toList());
        Page<T> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotal(list.size());
        page.setRecords(pageList);
        return page;

    }
}
