package com.cqq.stock.service;

import com.cqq.stock.entity.CalculateStockTransactionInfo;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.interfaces.StockAble;
import com.cqq.stock.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StockNewService {

    private StockService stockService;
    private PythonService pythonService;


    /**
     * 将网络中最新的股价导入到数据库中
     *
     * @param safely 是否以安全的形式导入 (不安全的形式即为 覆盖当日的数据)
     * @return long
     */
    public long syncDataFromNetwork(boolean safely) {

        List<StockTransactionInfo> allStockFromNowNetwork = new ArrayList<>(stockService.getAllStockFromNowNetwork());

        long date = TimeUtil.getDatetime();

        List<StockTransactionInfo> databaseStockMeanWhile = stockService.listByDate(date);
        if (databaseStockMeanWhile.size() != 0) {
            if (safely) {
                log.info("data already exist, maybe you need not safely operator,right ?");
                return -date;
            } else {
                log.info("data will be rewrite");
                stockService.deleteByDate(date);
            }
        }
//        Map<String, List<StockTransactionInfo>> map = databaseStockMeanWhile.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode));

        List<StockTransactionInfo> prepareToDatabaseList = allStockFromNowNetwork.stream()
//                .filter(alreadyNotExist(map))
//                .filter(s -> s.getClose() + s.getOpen() + s.getHigh() + s.getLow() != 0)
                .filter(Objects::nonNull)
                .filter(s -> s.getDate() != null)
                .filter(s -> s.getDate().equals(date))
                .collect(Collectors.toList());

        long time1 = System.currentTimeMillis();
        //3-4 minutes
        log.info("start get list After Date");
        List<StockTransactionInfo> afterDate = this.stockService.getListAfterDate(TimeUtil.offsetLeft(date, 60));
        long time2 = System.currentTimeMillis();
        System.out.println("get stock spend time:" + (time2 - time1));
        Map<String, List<StockTransactionInfo>> listMap = afterDate.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode));
        log.info("prepare to database stock number:{}", prepareToDatabaseList.size());
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
        QuicklyInsertUtil.quicklySaveToDatabase(prepareToDatabaseList.stream().map(s -> (StockAble) s).collect(Collectors.toList()));
        return date;
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
        long startDateL2 = TimeUtil.offsetLeft(startDateL1, 60);
        long endDateL2 = TimeUtil.offsetLeft(startDateL1, 1);
        TimingClock clock2 = new TimingClock("get stock from db begin");
        List<StockTransactionInfo> dbList = stockService.getListBetween(startDateL2, endDateL2);

        clock2.call("get stock from db success, speed: ");

        Map<String, List<StockTransactionInfo>> dbMap = dbList.stream()
                .collect(Collectors.groupingBy(StockTransactionInfo::getCode));
//<<<<<<< HEAD

        clock2.call("List<StockTransactionInfo> group by time:");

        List<CalculateStockTransactionInfo> allList = unionMap(deskMap, dbMap);
        deskMap = null;
        dbMap = null;

        clock2.call("unionMap time:");


        ArrayGroupUtil.batchDoing(allList, 100_0000, QuicklyInsertUtil::quicklySaveToDatabaseCalculateStockTransactionInfo);

        clock2.call("save data spend time: ");
/*
=======
        Set<String> deskKey = deskMap.keySet();
        List<CalculateStockTransactionInfo> allList = unionMap(deskMap, dbMap, deskKey);
        List<StockAble> lis = allList.stream().filter(s -> s.getDate() >= startDateL1 && s.getDate() <= endDateL1).collect(Collectors.toList());
        long time5 = System.currentTimeMillis();
        log.info("reshape data spend time: {} ms", time5 - time4);
        ArrayGroupUtil.batch(lis, 20000).forEach(QuicklyInsertUtil::quicklySaveToDatabase);
//        stockTransactionInfoService.saveBatch(lis,20000);
        long time6 = System.currentTimeMillis();
        log.info("save data spend time: {} ms", time6 - time5);
>>>>>>> 6aa66ec33b51318994156354de6ec5ffc0cfa6de
*/


    }

    private List<CalculateStockTransactionInfo> unionMap(Map<String, List<StockTransactionInfo>> deskMap, Map<String, List<StockTransactionInfo>> dbMap) {
        Set<String> deskKey = deskMap.keySet();
        new TimingClock("start union");
        List<List<CalculateStockTransactionInfo>> collect = deskKey.stream().parallel().map(code -> {
            List<StockTransactionInfo> tempDeskList = deskMap.get(code);
            List<StockTransactionInfo> tempDbList = dbMap.get(code);
            if (tempDbList != null) {
                if (tempDeskList != null) {

                    tempDeskList.addAll(tempDbList);
                }
            }
            if (tempDeskList != null) {

                tempDeskList.sort(Comparator.comparing(StockTransactionInfo::getDate));
                return CciUtil.main(tempDeskList);
            } else {
                return null;
            }

        }).filter(Objects::nonNull).collect(Collectors.toList());

        return collect.stream().reduce(new ArrayList<>(), (l, r) -> {
            l.addAll(r);
            return l;
        });

    }

    private void deleteData(long startDateL1, int endTimeL1) {
        log.info("delete data start");
        long time1 = System.currentTimeMillis();
        this.stockService.deleteBetweenDate(startDateL1, endTimeL1);
        long time2 = System.currentTimeMillis();
        log.info("delete data over, speed time : {}ms", (time2 - time1));
    }


    /**
     * 走通整个流程
     */
    public void process() {

        long date = this.syncDataFromNetwork(true);
        boolean hasData = false;
        if (date == -1) {
            log.info("occur exception");
            return;
        }
        log.info("select code list in condition");
        long time1 = System.currentTimeMillis();
        List<String> codeList = stockService.selectCCILowByDate(date);
        long time2 = System.currentTimeMillis();
        log.info("select codeList speed time: {}ms, exist size:{}", time2 - time1, codeList.size());

        log.info("select transactionInfo in codeList");
        long time3 = System.currentTimeMillis();
        List<StockTransactionInfo> stockTransactionInfoByCode = stockService.getStockTransactionInfoByCode(codeList);
        long time4 = System.currentTimeMillis();
        log.info("select transactionInfo ok, speed time: {}ms  list.size:{}:", time4 - time3, stockTransactionInfoByCode.size());

        stockTransactionInfoByCode.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode)).forEach((code, list) -> {
            if (code == null) {
                log.info("code is null, out ");
                return;
            }
            if (StockJudgeUtil.notBuy(code)) {
                log.info("code not  buy, out {}", code);
                return;
            }

            if (list == null || list.size() == 0 || list.size() < 100) {
                log.info("list null or size = 0 or size < 100 out:{}", code);
                return;
            }
            Long newlyDate = list.get(list.size() - 1).getDate();
            if (!newlyDate.equals(date)) {
                log.info("date is not same, the data is old");
                return;
            }
            if (list.get(list.size() - 1).getClose() > 5000) {
                log.info("money to low, can not buy out:" + code);
                return;
            }
            list.sort(Comparator.comparing(StockTransactionInfo::getDate));
            List<StockTransactionInfo> collect = list.stream().skip(28).collect(Collectors.toList());
            try {
                long time5 = System.currentTimeMillis();
                log.info("start make data record " + code);
                MakeDataUtil.generateX(collect, date + "", code);
                MakeDataUtil.generateY(collect, date + "", code);
                MakeDataUtil.generateTestData(collect, date + "", code);
                MakeDataUtil.generateOtherDir(date + "", "D:\\newstock\\{date}\\result\\", "D:\\newstock\\{date}\\param\\");
                long time6 = System.currentTimeMillis();
                log.info("spend time: {}ms", (time6 - time5));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        try {
            log.info("start call python");
            long time7 = System.currentTimeMillis();
            pythonService.call(date);
            long time8 = System.currentTimeMillis();
            log.info("call python success, spend time:{}", time8 - time7);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }


    public List<String> getPrice(String code, long date) {
        try {
            System.out.println("hello world");
            File file = new File("D:\\newstock\\" + date + "\\result\\" + code + ".txt");
            List<String> values = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
                StringBuilder newValue = new StringBuilder();
                int pos = s.lastIndexOf("%");
                for (int i = pos - 1; i >= 0; i--) {
                    char c = s.charAt(i);
                    if (c >= '0' && c <= '9') {
                        newValue.append(c);
                    } else if (c == '.') {
                        newValue.append(c);

                    } else {
                        break;
                    }
                }
                values.add(newValue.reverse().toString());
            }
            return values;
        } catch (Exception e) {
            return null;
        }

    }

}
