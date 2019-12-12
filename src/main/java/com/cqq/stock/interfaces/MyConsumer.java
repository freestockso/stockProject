package com.cqq.stock.interfaces;

import com.cqq.stock.entity.StockRecent;

public interface MyConsumer {
    void doing(StockRecent stockInfo, String value);
}
