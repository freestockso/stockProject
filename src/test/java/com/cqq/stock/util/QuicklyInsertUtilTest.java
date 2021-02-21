package com.cqq.stock.util;

import com.cqq.stock.entity.Stock;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.interfaces.StockAble;
import net.minidev.json.annotate.JsonIgnore;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuicklyInsertUtilTest {

    public static final int STEP = 1000;

    /**
     * 使用ReadUtil读取本地磁盘的股票数据
     * 使用QuicklyInsert将内存插入数据库
     */
    @Test
    @JsonIgnore
    public void quicklySaveToDatabase() {
        long d1 = System.currentTimeMillis();

        for (int i = 0; i <= 6000; i += STEP) {
            List<StockTransactionInfo> list = ReadUtil.readStockList(i, STEP, s -> s.getDate() > 2012_0000, f -> true).values()
                    .stream()
                    .map(ls -> TimeUtil.doingSomething(ls, CciUtil::main, "cci:main"))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            ArrayGroupUtil.batch(list, 50_0000).forEach(ls -> {
                long time1 = System.currentTimeMillis();
                QuicklyInsertUtil.quicklySaveToDatabase(ls.stream().map(s -> (StockAble) s).collect(Collectors.toList()));
                long time2 = System.currentTimeMillis();
                System.out.println("spend Time:" + (time2 - time1));
            });

        }
        long time2 = System.currentTimeMillis();
        System.out.println("allTime" + (time2 - d1));
    }

    @Test
    public void test12() {
        Map<String, List<Stock>> stringListMap = ReadUtil.readStockList(0, 1, s -> s.getDate() > 2012_0000, f->f.getName().contains("570008"));
        System.out.println(stringListMap);

    }
}