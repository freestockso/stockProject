package com.cqq.stock.util;

import com.cqq.stock.entity.EMAStock;
import com.cqq.stock.entity.StockTransactionInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EMACalculateUtilTest {


    public List<StockTransactionInfo> getList() {
        List<StockTransactionInfo> list = new ArrayList<>();
        long[] values = new long[]{
                6433, 5740, 6799, 7102, 6734, 7660, 7829, 8390, 9488, 9800, 10000, 10140, 10055, 9020, 8750, 8410, 8815, 9130, 9548, 9167,
                8620, 8517, 7830, 7600, 7398, 7550, 7641, 7408, 7399, 7340, 7420, 7859, 7649, 7620, 7178,
                7174, 7175, 7189, 6827, 6865, 6753, 7105, 7700, 7730, 7520, 7618, 7735, 6989, 8180, 7880, 7001, 6941, 7207, 7190, 7299, 7610, 7792, 7675,
        };//12,
        Arrays.stream(values).forEach(v -> addElement2List(list, v / 100));
        return list;
    }

    private void addElement2List(List<StockTransactionInfo> list, long close) {
        StockTransactionInfo e = new StockTransactionInfo();
        e.setClose(close);
        list.add(e);
    }


    @Test
    public void testListSubList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list.add(i);
        }
        System.out.println(list.subList(list.size() - 5, list.size()));
//        System.out.println(list.subList(0,10));
    }


    @Test
    public void aa() {
        List<EMAStock> list = getList().stream().map(EMAStock::new).collect(Collectors.toList());
        EMACalculateUtil.calculateAll(list, 12, 26, 9);
        list.forEach(System.out::println);

    }

    @Test
    public void ans() {
        int n = 11;
        int a[] = new int[n * 2 + 5];
        for (int i = 0; i < n; i++) {
            a[n - i] = a[n + i] = n - i;
        }
        a[2 * n ] = 0;
        for (int i = 1; i <= 2 * n - 1; i++) {
            a[i + 1] += a[i] / 10;
            a[i] = a[i] % 10;
        }
        for (int i = 2 * n - 1; i >= 1; i--) {
            System.out.print(a[i]);
        }
    }

}

