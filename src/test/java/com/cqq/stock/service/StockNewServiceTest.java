package com.cqq.stock.service;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class StockNewServiceTest {


    @Test
    public void getPrice() throws IOException {
        StockNewService stockNewService = new StockNewService(null,null,null);
//        StockNewService stockNewService = new StockNewService();
        List<String> sz002634 = stockNewService.getPrice("sz002634", 2020_01_10);
        assert sz002634.size() != 0;
    }
}