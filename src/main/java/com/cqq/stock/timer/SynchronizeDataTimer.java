package com.cqq.stock.timer;

import com.cqq.stock.constants.StockConstant;
import com.cqq.stock.entity.ListEntity;
import com.cqq.stock.entity.StockInfo;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.service.NoticeService;
import com.cqq.stock.service.StockService;
import com.cqq.stock.util.QuicklyInsertUtil;
import com.cqq.stock.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class SynchronizeDataTimer {

    private StockService stockService;
    private NoticeService noticeService;

    /**
     * 每天下午3:30执行一次
     * 数据同步的定时任务,每次把最新的数据同步到库中
     */
    @Scheduled(cron = "0 30 15 * * ?")
    public void syncData() {
        List<StockTransactionInfo> todayAllStockInfoFromNetwork = new ArrayList<>(stockService.getTodayAllStockInfoFromNetwork());

        Map<String, StockInfo> stockMap = stockService.getStockMap();

        Long date = todayAllStockInfoFromNetwork.size() == 0 ? 0 : todayAllStockInfoFromNetwork.get(0).getDate();

        List<StockTransactionInfo> listByToday = stockService.getListByToday(String.valueOf(date));
        Map<String, List<StockTransactionInfo>> map = listByToday.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode));

        List<StockTransactionInfo> prepareToDatabaseList = todayAllStockInfoFromNetwork.stream().filter(s -> {
            List<StockTransactionInfo> stockTransactionInfos = map.get(s.getCode());
            return stockTransactionInfos == null || stockTransactionInfos.isEmpty();
        }).filter(s -> s.getClose() + s.getOpen() + s.getHigh() + s.getLow() != 0)
                .filter(s -> s.getDate().equals(date))
                .peek(s -> {
                    StockInfo stockInfo = stockMap.get(s.getCode());
                    s.setLow(s.getLow() / (stockInfo.getTen()));
                    s.setHigh(s.getHigh() / (stockInfo.getTen()));
                    s.setOpen(s.getOpen() / (stockInfo.getTen()));
                    s.setClose(s.getClose() / (stockInfo.getTen()));
                }).collect(Collectors.toList());

        log.info("prepare to database stock number:{}", prepareToDatabaseList.size());
        QuicklyInsertUtil.main(prepareToDatabaseList);
    }


    /**
     * 如果有十倍的数据差距，那么就将ten置为10
     * 否则置为1
     */
//    @Scheduled(cron = "0/15 * * * * ? ")
    public void ifThereIsATenfoldGap() {
        int sum = 0;
        int equalSum = 0;
        List<StockTransactionInfo> networkData = stockService.getTodayAllStockInfoFromNetwork();
        List<StockTransactionInfo> databaseData = stockService.getListByToday("20191014");
        HashSet<String> set = new HashSet<>();
        for (StockTransactionInfo net : networkData) {
            for (StockTransactionInfo data : databaseData) {
                if (net.getCode() == null) continue;
                if (data.getCode() == null) continue;
                if (net.getCode().equals(data.getCode())) {
                    equalSum++;
                    if (isNotEqual(net.getClose(), data.getClose())) {
                        sum++;
                        set.add(data.getCode());
                    } else if (isNotEqual(net.getHigh(), data.getHigh())) {
                        System.out.println(String.format("code error for high in %s", net.getCode()));
                        System.out.println(net.getHigh() + "---" + data.getHigh());
                        sum++;

                    } else if (isNotEqual(net.getLow(), data.getLow())) {
                        System.out.println(String.format("code error for low in %s", net.getCode()));
                        System.out.println(net.getLow() + "---" + data.getLow());
                        sum++;

                    } else if (isNotEqual(net.getOpen(), data.getOpen())) {
                        System.out.println(String.format("code error for open in %s", net.getCode()));
                        System.out.println(net.getOpen() + "---" + data.getOpen());
                        sum++;
                    }

                }

            }
        }
        System.out.println(equalSum + " " + sum);

        set.forEach(o -> {
            stockService.setTenTo1(o);
            System.out.println(o);
        });
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


    private boolean isNotEqual(Long value1, Long value2) {
        double multiple = value1 * 1.0 / value2;
        return multiple < 0.2 || multiple > 5;
    }

    /**
     * 计算股票明日的最好和最坏买卖点
     * 16:00时,进行计算
     */
    @Scheduled(cron = "0 0 16 * * ?")
    public void autoGuessAll() {
        stockService.autoGuessAll();
    }

    /**
     * 提醒用户出售的定时任务
     *
     */
    @Scheduled(cron = "0 30/2 9-13 * * ?")
    public void noticeToSale() {

        noticeService.noticeToSale(null);

    }

    /**
     * 提醒用户出售的定时任务
     *
     */
//    @Scheduled(cron = "0 30/2 9-13 * * ?")
    @Scheduled(cron = "0 30/1 * * * ?")
    public void noticeToBuy() {
         noticeService.noticeToBuy(null);

    }
}
