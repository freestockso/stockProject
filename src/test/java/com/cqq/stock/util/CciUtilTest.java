package com.cqq.stock.util;

import com.cqq.stock.entity.CalculateStockTransactionInfo;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.interfaces.StockAble;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CciUtilTest {

    @Test
    public void main() {
        ArrayList<StockAble> stockTransactionInfos = new ArrayList<>();

        stockTransactionInfos.add(new StockTransactionInfo(2394L, 2420L, 2385L, 2389L));
        stockTransactionInfos.add(new StockTransactionInfo(2385L, 2407L, 2372L, 2395L));
        stockTransactionInfos.add(new StockTransactionInfo(2394L, 2404L, 2364L, 2367L));
        stockTransactionInfos.add(new StockTransactionInfo(2373L, 2387L, 2337L, 2378L));
        stockTransactionInfos.add(new StockTransactionInfo(2360L, 2367L, 2346L, 2350L));
        stockTransactionInfos.add(new StockTransactionInfo(2346L, 2359L, 2318L, 2332L));
        stockTransactionInfos.add(new StockTransactionInfo(2353L, 2380L, 2340L, 2375L));
        stockTransactionInfos.add(new StockTransactionInfo(2373L, 2380L, 2357L, 2379L));
        stockTransactionInfos.add(new StockTransactionInfo(2409L, 2430L, 2405L, 2414L));
        stockTransactionInfos.add(new StockTransactionInfo(2395L, 2415L, 2377L, 2381L));
        stockTransactionInfos.add(new StockTransactionInfo(2392L, 2405L, 2360L, 2378L));
        stockTransactionInfos.add(new StockTransactionInfo(2404L, 2406L, 2384L, 2386L));
        stockTransactionInfos.add(new StockTransactionInfo(2383L, 2388L, 2364L, 2370L));
        stockTransactionInfos.add(new StockTransactionInfo(2405L, 2514L, 2394L, 2496L));
        stockTransactionInfos.add(new StockTransactionInfo(2489L, 2520L, 2474L, 2488L));
        stockTransactionInfos.add(new StockTransactionInfo(2495L, 2507L, 2477L, 2496L));
        stockTransactionInfos.add(new StockTransactionInfo(2491L, 2522L, 2490L, 2518L));
        stockTransactionInfos.add(new StockTransactionInfo(2524L, 2537L, 2493L, 2507L));
        stockTransactionInfos.add(new StockTransactionInfo(2513L, 2536L, 2496L, 2527L));
        stockTransactionInfos.add(new StockTransactionInfo(2526L, 2526L, 2493L, 2500L));
        stockTransactionInfos.add(new StockTransactionInfo(2474L, 2482L, 2421L, 2446L));
        stockTransactionInfos.add(new StockTransactionInfo(2436L, 2444L, 2421L, 2428L));
        stockTransactionInfos.add(new StockTransactionInfo(2449L, 2465L, 2443L, 2462L));
        stockTransactionInfos.add(new StockTransactionInfo(2470L, 2484L, 2444L, 2458L));
        stockTransactionInfos.add(new StockTransactionInfo(2465L, 2475L, 2420L, 2453L));
        stockTransactionInfos.add(new StockTransactionInfo(2448L, 2451L, 2425L, 2435L));
        stockTransactionInfos.add(new StockTransactionInfo(2446L, 2468L, 2421L, 2434L));
        stockTransactionInfos.add(new StockTransactionInfo(2462L, 2467L, 2415L, 2423L));
        stockTransactionInfos.add(new StockTransactionInfo(2381L, 2384L, 2363L, 2376L));
        stockTransactionInfos.add(new StockTransactionInfo(2391L, 2430L, 2376L, 2420L));

        List<CalculateStockTransactionInfo> main = CciUtil.main(stockTransactionInfos);
        main.forEach(System.out::println);
    }
}