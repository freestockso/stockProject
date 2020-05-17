package com.cqq.stock.service;

import com.cqq.stock.entity.CalculateStockTransactionInfo;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.interfaces.StockAble;
import com.cqq.stock.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StockNewService {

    private StockService stockService;


    /**
     * 将网络中最新的股价导入到数据库中
     *
     * @param safely 是否以安全的形式导入 (不安全的形式即为 覆盖当日的数据)
     */
    public void syncDataFromNetwork(boolean safely) {

        List<StockTransactionInfo> allStockFromNowNetwork = new ArrayList<>(stockService.getAllStockFromNowNetwork());

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
                .collect(Collectors.toList());

        log.info("prepare to database stock number:{}", prepareToDatabaseList.size());
        long time1 = System.currentTimeMillis();
        //3 minutes
        List<StockTransactionInfo> afterDate = this.stockService.getListAfterDate(TimeUtil.offset(date, 60));
        long time2 = System.currentTimeMillis();
        System.out.println("spend time:" + (time2 - time1));
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
        prepareToDatabaseList.forEach(System.out::println);
        QuicklyInsertUtil.quicklySaveToDatabase(prepareToDatabaseList.stream().map(s -> (StockAble) s).collect(Collectors.toList()));
    }

    private Predicate<StockTransactionInfo> alreadyNotExist(Map<String, List<StockTransactionInfo>> map) {
        return s -> {
            List<StockTransactionInfo> stockTransactionInfos = map.get(s.getCode());
            return stockTransactionInfos == null || stockTransactionInfos.isEmpty();
        };
    }

    private StockTransactionInfoService stockTransactionInfoService;


    public void syncDataByDesk(long startDateL1) {
        int endDateL1 = TimeUtil.getDatetime();
        TimingClock timingClock = new TimingClock("get stock info from desk, maybe need 30,000ms");
        Map<String, List<StockTransactionInfo>> deskMap = QuicklyReadUtil.stockMap(startDateL1, endDateL1);
        timingClock.call("get stock from desk success, real need:");
        if (deskMap.keySet().size() == 0) {
            log.info("nothing !!!!!!!!!!!!! what hanpend ?");
            return;
        }
        deleteData(startDateL1, endDateL1);
        long startDateL2 = TimeUtil.offset(startDateL1, 60);
        long endDateL2 = TimeUtil.offset(startDateL1, 1);
        TimingClock clock2 = new TimingClock("get stock from db begin");
        List<StockTransactionInfo> dbList = stockService.getListBetween(startDateL2, endDateL2);

        clock2.call("get stock from db success, speed: ");

        Map<String, List<StockTransactionInfo>> dbMap = dbList.stream()
                .collect(Collectors.groupingBy(StockTransactionInfo::getCode));

        clock2.call("List<StockTransactionInfo> group by time:");

        List<CalculateStockTransactionInfo> allList = unionMap(deskMap, dbMap);
        deskMap = null;
        dbMap = null;

        clock2.call("unionMap time:");


        ArrayGroupUtil.batchDoing(allList,100_0000, QuicklyInsertUtil::quicklySaveToDatabaseCalculateStockTransactionInfo);

        clock2.call("save data spend time: ");


    }

    private List<CalculateStockTransactionInfo> unionMap(Map<String, List<StockTransactionInfo>> deskMap, Map<String, List<StockTransactionInfo>> dbMap) {
        Set<String> deskKey = deskMap.keySet();
        TimingClock timingClock = new TimingClock("start union");
        List<List<CalculateStockTransactionInfo>> collect = deskKey.stream().parallel().map(code -> {
            List<StockTransactionInfo> tempDeskList = deskMap.get(code);
            List<StockTransactionInfo> tempDbList = dbMap.get(code);
            if (tempDbList != null) {
                if (tempDeskList != null) {

                    tempDeskList.addAll(tempDbList);
                }
            }
            tempDeskList.sort(Comparator.comparing(StockTransactionInfo::getDate));
            List<CalculateStockTransactionInfo> main = CciUtil.main(tempDeskList);
//            List<CalculateStockTransactionInfo> list = new ArrayList<>(main);
            return main;

        }).collect(Collectors.toList());
        List<CalculateStockTransactionInfo> result = collect.stream().reduce(new ArrayList<>(), (l, r) -> {
            l.addAll(r);
            return l;
        });

        return result;

    }

    private void deleteData(long startDateL1, int endTimeL1) {
        log.info("delete data start");
        long time1 = System.currentTimeMillis();
        this.stockService.deleteBetweenDate(startDateL1, endTimeL1);
        long time2 = System.currentTimeMillis();
        log.info("delete data over, speed time : {}ms", (time2 - time1));
    }
}
