package com.cqq.stock.timer;

import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.service.NoticeService;
import com.cqq.stock.service.StockNewService;
import com.cqq.stock.service.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class SynchronizeDataTimer {

    private StockService stockService;
    private NoticeService noticeService;
    private StockNewService stockNewService;

    /**
     * 每天下午3:30执行一次
     * 数据同步的定时任务,每次把最新的数据同步到库中
     */
    @Scheduled(cron = "0 30 15 * * ?")
    public void syncData() {
        stockNewService.syncDataFromNetwork(true);
    }


    /**
     * 如果有十倍的数据差距，那么就将ten置为10
     * 否则置为1
     */
//    @Scheduled(cron = "0/15 * * * * ? ")
    public void ifThereIsATenfoldGap() {
        int sum = 0;
        int equalSum = 0;
        List<StockTransactionInfo> networkData = stockService.getAllStockFromNowNetwork();
        List<StockTransactionInfo> databaseData = stockService.listByDate(20191014);
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
     */
    @Scheduled(cron = "0 30/2 9-13 * * ?")
    public void noticeToSale() {

        noticeService.noticeToSale(null);

    }

    /**
     * 提醒用户出售的定时任务
     */
//    @Scheduled(cron = "0 30/2 9-13 * * ?")
    @Scheduled(cron = "0 30/1 * * * ?")
    public void noticeToBuy() {
        noticeService.noticeToBuy(null);

    }
}
