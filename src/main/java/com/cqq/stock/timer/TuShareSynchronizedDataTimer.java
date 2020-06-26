package com.cqq.stock.timer;

import com.cqq.stock.constants.StockConfig;
import com.cqq.stock.entity.po.StockDataRecord;
import com.cqq.stock.entity.vo.R;
import com.cqq.stock.service.StockDataRecordService;
import com.cqq.stock.util.PythonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class TuShareSynchronizedDataTimer {

    private StockDataRecordService stockDataRecordService;
    private ExecutorService fixedThreadPool;

    private StockConfig stockConfig;

    /**
     * 将远端数据同步到本地磁盘
     * 每5分钟更新一次
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void syncDataFromNetworkToDesk() {
        Set<Long> existInDesk = new HashSet<>(stockDataRecordService.getDateList());
        List<Long> allMaybe = getAllMaybe();

        //获取需要同步的日期
        List<Long> dateList = allMaybe.stream().filter(s -> !existInDesk.contains(s)).limit(300).collect(Collectors.toList());

        if (dateList.isEmpty()) {
            log.info("所有数据已经同步完毕,不需要再进行同步");
            return;
        }

        dateList.forEach(datetime -> {
            final String date = String.valueOf(datetime);
            fixedThreadPool.submit(() -> {
                StockDataRecord entity = new StockDataRecord(Integer.valueOf(date));
                try {
                    R<String> stringR = PythonUtil.callPython(stockConfig.getDayStockScriptPath(), stockConfig.getDayStockExePath(),
                            stockConfig.getDayStockPath().replace("{date}", date), date, date);

                    entity.setMsg(stringR.getMsg());
                    this.stockDataRecordService.save(entity);
                } catch (IOException | InterruptedException e) {
                    log.error(e.toString());
                    entity.setMsg(e.getClass().toString() + ":" + e.toString());
                    this.stockDataRecordService.save(entity);
                }
            });
        });


    }


    /**
     * 获取从20150101 - 至今为止所有的日期
     *
     * @return list
     */
    private List<Long> getAllMaybe() {

        long maxDate = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        long time = System.currentTimeMillis();


        Calendar instance = Calendar.getInstance();
        List<Long> list = new ArrayList<>();
        instance.set(2015, Calendar.JANUARY, 1);
        long timeInMillis = instance.getTimeInMillis();
        for (long i = timeInMillis; i <= time; i += 24 * 60 * 60 * 1000) {
            Date date = new Date();
            date.setTime(i);
            long yyyyMMdd = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(date));

            if (yyyyMMdd < maxDate) {
                list.add(yyyyMMdd);
            }
        }
        long format = Long.parseLong(new SimpleDateFormat("HHmm").format(new Date()));
        if (format >= 17_00) {
            list.add(maxDate);

        }
        return list;
    }
}
