package com.cqq.stock.timer;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cqq.stock.constants.StockConfig;
import com.cqq.stock.entity.po.StockDataRecord;
import com.cqq.stock.entity.po.StockDayData;
import com.cqq.stock.entity.vo.R;
import com.cqq.stock.service.StockDataRecordService;
import com.cqq.stock.service.StockDayDataService;
import com.cqq.stock.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@AllArgsConstructor
public class TuShareSynchronizedDataTimer {

    private StockDataRecordService stockDataRecordService;
    private StockDayDataService stockDayDataService;
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

    /**
     * 将本地磁盘的数据同步到数据库
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void desk2DB() {
        //找到所有未存入数据库的数据
        List<StockDataRecord> records = this.stockDataRecordService.list(Wrappers.<StockDataRecord>query().lambda().eq(StockDataRecord::getInDb, 0))
                .stream()
                .sorted(Comparator.comparing(StockDataRecord::getDate))
                .limit(300)
                .collect(Collectors.toList());
        if (records.isEmpty()) {
            log.info("不需要将本地磁盘的数据同步到数据库");
            return;
        }
        Integer minDate = records.get(0).getDate();
        Integer maxDate = records.get(records.size() -1).getDate();
        final long beginDate = TimeUtil.offsetLeft(minDate, 90);
        final long endDate = TimeUtil.offsetLeft(minDate, 1);
        //从库里找到之前的数据，方便计算前几个CCI
        //60 day * 5000 = 30w data
        List<List<StockDayData>> list = getMapInDBAndDesk(records, beginDate, endDate);


        list.forEach(ls->fixedThreadPool.submit(()->{
            List<StockDayData> realInDB = ls.stream().filter(s -> s.getDate() >= minDate).collect(Collectors.toList());
            this.stockDayDataService.saveBatch(realInDB);
        }));
        LambdaUpdateWrapper<StockDataRecord> eq = Wrappers.<StockDataRecord>update()
                .lambda()
                .ge(StockDataRecord::getDate, minDate)
                .le(StockDataRecord::getDate, maxDate)
                .set(StockDataRecord::getInDb, 1);
        this.stockDataRecordService.update(eq);

    }

    private  List<List<StockDayData>> getMapInDBAndDesk(List<StockDataRecord> records, long beginDate, long endDate) {
        List<StockDayData> dbList = this.stockDayDataService.list(Wrappers.<StockDayData>query().lambda().ge(StockDayData::getDate, beginDate).le(StockDayData::getDate, endDate));
        List<StockDayData> allList = records.stream()
                .map(StockDataRecord::getDate)
                .map(date -> {
                    String filePath = stockConfig.getDayStockDataTxt().replace("{date}", String.valueOf(date));
                    return StockDayDataReadUtil.readStockDayData(filePath);
                }).reduce(new ArrayList<>(), (l, r) -> {
                    l.addAll(r);
                    return l;
                });
        allList.addAll(dbList);
        Map<String, List<StockDayData>> collect = allList.stream().collect(Collectors.groupingBy(StockDayData::getCode));
        collect.forEach((k,v)->{
            v.sort(Comparator.comparing(StockDayData::getDate));
            CciUtilV2.calculateAllCCI(v);
        });
        return ArrayGroupUtil.batch(allList,10_0000 );
    }
}
