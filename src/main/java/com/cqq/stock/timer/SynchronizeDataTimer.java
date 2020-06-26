package com.cqq.stock.timer;

import com.cqq.stock.service.NoticeService;
import com.cqq.stock.service.StockNewService;
import com.cqq.stock.service.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class SynchronizeDataTimer {

    private StockService stockService;
    private NoticeService noticeService;
    private StockNewService stockNewService;

//    private StockNewService stockNewService;

    /**
     * 每天下午3:30执行一次
     * 数据同步的定时任务,每次把最新的数据同步到库中
     */
//    @Scheduled(cron = "0 30 15 * * ?")
    public void syncData() {
        stockNewService.process();
    }


    /**
     * 计算股票明日的最好和最坏买卖点
     * 16:00时,进行计算
     */
//    @Scheduled(cron = "0 0 16 * * ?")
    public void autoGuessAll() {
        stockService.autoGuessAll();
    }

    /**
     * 提醒用户出售的定时任务
     */
//    @Scheduled(cron = "0 30/2 9-13 * * ?")
    public void noticeToSale() {

        noticeService.noticeToSale(null);

    }

    /**
     * 提醒用户出售的定时任务
     */
//    @Scheduled(cron = "0 30/2 9-13 * * ?")
//    @Scheduled(cron = "0 30/1 * * * ?")
    public void noticeToBuy() {
        noticeService.noticeToBuy(null);

    }
}
