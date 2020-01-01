package com.cqq.stock.service;

import com.cqq.stock.entity.CalculateStockTransactionInfo;
import com.cqq.stock.entity.StockInfo;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.interfaces.StockAble;
import com.cqq.stock.mapper.StockTransactionInfoMapper;
import com.cqq.stock.util.CciUtil;
import com.cqq.stock.util.QuicklyInsertUtil;
import com.cqq.stock.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StockNewService {

    private StockTransactionInfoMapper stockTransactionInfoMapper;
    private StockService stockService;

    public void syncDataFromNetwork(boolean safely) {

        List<StockTransactionInfo> allStockFromNowNetwork = new ArrayList<>(stockService.getAllStockFromNowNetwork());

        Map<String, StockInfo> stockMap = stockService.getStockMap();

        long date = allStockFromNowNetwork.size() == 0 ? 0 : allStockFromNowNetwork.get(0).getDate();

        List<StockTransactionInfo> databaseStockMeanWhile = stockService.listByDate(date);
        if (databaseStockMeanWhile.size() != 0) {
            if (safely) {
                log.info("data already exist, maybe you need not safely operator,right ?");
                return;
            } else {
                log.info("data will be rewrite");
                stockService.deleteByDate(date);
            }


        }
        Map<String, List<StockTransactionInfo>> map = databaseStockMeanWhile.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode));

        List<StockTransactionInfo> prepareToDatabaseList = allStockFromNowNetwork.stream()
                .filter(alreadyNotExist(map))
                .filter(s -> s.getClose() + s.getOpen() + s.getHigh() + s.getLow() != 0)
                .filter(s -> s.getDate().equals(date))
                .peek(s -> {
                    StockInfo stockInfo = stockMap.get(s.getCode());
                    s.setLow(s.getLow() / (stockInfo.getTen()));
                    s.setHigh(s.getHigh() / (stockInfo.getTen()));
                    s.setOpen(s.getOpen() / (stockInfo.getTen()));
                    s.setClose(s.getClose() / (stockInfo.getTen()));
                }).collect(Collectors.toList());

        log.info("prepare to database stock number:{}", prepareToDatabaseList.size());
        long time1 = System.currentTimeMillis();
        List<StockTransactionInfo> afterDate = this.stockService.getListAfterDate(TimeUtil.offset(date, 60));
        long time2 = System.currentTimeMillis();
        System.out.println("sp:" + (time2 - time1));
        Map<String, List<StockTransactionInfo>> listMap = afterDate.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode));
        for (int i = 0; i < prepareToDatabaseList.size(); i++) {
            StockTransactionInfo stockTransactionInfo = prepareToDatabaseList.get(i);
            List<StockTransactionInfo> list = listMap.get(stockTransactionInfo.getCode());
            if (list != null) {
                list.sort(Comparator.comparing(StockTransactionInfo::getDate));
                list.add(stockTransactionInfo);
                List<CalculateStockTransactionInfo> ls = CciUtil.main(list);
                if (ls != null && !list.isEmpty()) {
                    CalculateStockTransactionInfo s = ls.get(ls.size() - 1);
                    prepareToDatabaseList.set(i, s);
                } else {
                    stockTransactionInfo.setCci(10000.0);
                    prepareToDatabaseList.set(i, stockTransactionInfo);
                }
            }

        }
        prepareToDatabaseList.stream().forEach(System.out::println);
        QuicklyInsertUtil.quicklySaveToDatabase(prepareToDatabaseList.stream().map(s -> (StockAble) s).collect(Collectors.toList()));
    }

    private Predicate<StockTransactionInfo> alreadyNotExist(Map<String, List<StockTransactionInfo>> map) {
        return s -> {
            List<StockTransactionInfo> stockTransactionInfos = map.get(s.getCode());
            return stockTransactionInfos == null || stockTransactionInfos.isEmpty();
        };
    }


}
