package com.cqq.stock.util;

import com.cqq.stock.entity.po.StockDayData;
import org.junit.Test;

import java.util.List;

public class StockDayDataReadUtilTest {

    @Test
    public void hello5() {
        List<StockDayData> stockDayData = StockDayDataReadUtil.readStockDayData("D:\\data\\stock\\day\\20150107.txt");
        List<StockDayData> l2 = StockDayDataReadUtil.readStockDayData("D:\\data\\stock\\day\\20150111.txt");
        System.out.println("");

    }
}
