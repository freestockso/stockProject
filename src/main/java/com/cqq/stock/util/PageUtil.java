package com.cqq.stock.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.stock.interfaces.PageAble;

public class PageUtil {
    public static <T> Page<T> toPage(PageAble condition) {
        return new Page<>(condition.getCurrent(), condition.getLimit());
    }
}
