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
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class TuShareSynchronizedDataTimer {

    private StockDataRecordService stockDataRecordService;
    private StockDayDataService stockDayDataService;
    private ExecutorService fixedThreadPool;

    private StockConfig stockConfig;
    private DataSourceProperties dataSourceProperties;

    /**
     * 将远端数据增量同步到本地磁盘
     * 这个速度超级慢,只能用于后期的单日新增
     * 每1分钟更新一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncDataFromNetworkToDesk() {
        Set<Long> existInDesk = new HashSet<>(stockDataRecordService.getDateList());
        List<Long> allMaybe = getAllMaybe();

        //获取需要同步的日期
        List<Long> dateList = allMaybe.stream().filter(s -> !existInDesk.contains(s)).limit(400).collect(Collectors.toList());

        if (dateList.isEmpty()) {
            log.info("远端数据已经同步完毕,不需要再进行同步");
            return;
        }
        log.info("远端数据开始同步,请等待...");

        String fatherDir = stockConfig.getDayStockPath().substring(0, stockConfig.getDayStockPath().lastIndexOf('\\'));
        File fatherFile = new File(fatherDir);
        if (!fatherFile.exists()) {
            fatherFile.mkdirs();
        }
        TimingClock timingClock = new TimingClock();
        dateList.forEach(datetime -> {
            final String date = String.valueOf(datetime);
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
        timingClock.call("远端数据同步完成");
    }


    /**
     * 使用多线程 加快远端同步到本地的效率
     * 但是不可避免的可能发生数据错乱,或者使用失败等奇怪的异常
     */
    public synchronized void syncDataFromNetworkToDeskQuickly() throws InterruptedException {
        Set<Long> existInDesk = new HashSet<>(stockDataRecordService.getDateList());
        List<Long> allMaybe = getAllMaybe();

        //获取需要同步的日期
        List<Long> dateList = allMaybe.stream().filter(s -> !existInDesk.contains(s)).limit(3000).collect(Collectors.toList());

        if (dateList.isEmpty()) {
            log.info("远端数据已经同步完毕,不需要再进行同步");
            return;
        }
        log.info("远端数据开始同步,请等待...");

        String fatherDir = stockConfig.getDayStockPath().substring(0, stockConfig.getDayStockPath().lastIndexOf('\\'));
        File fatherFile = new File(fatherDir);
        if (!fatherFile.exists()) {
            fatherFile.mkdirs();
        }

        TimingClock timingClock = new TimingClock();
        CountDownLatch countDownLatch = new CountDownLatch(dateList.size());
        dateList.forEach(datetime -> fixedThreadPool.submit(() -> {
            while (true) {
                final String date = String.valueOf(datetime);
                StockDataRecord entity = new StockDataRecord(Integer.valueOf(date));
                try {
                    R<String> stringR = PythonUtil.callPython(stockConfig.getDayStockScriptPath(), stockConfig.getDayStockExePath(),
                            stockConfig.getDayStockPath().replace("{date}", date), date, date);

                    entity.setMsg(stringR.getMsg());
                    if (entity.getMsg().equals("no data") || entity.getMsg().equals("success")) {
                        this.stockDataRecordService.save(entity);
                        break;
                    } else {
                        System.out.println(Thread.currentThread().getName() + " 出现问题,开始睡觉...");
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName() + " 醒来了!!!");

                    }
                } catch (IOException | InterruptedException e) {
                    log.error(e.toString());
//                    entity.setMsg(e.getClass().toString() + ":" + e.toString());
//                    this.stockDataRecordService.save(entity);
                }
            }
            countDownLatch.countDown();

        }));
        //必须得等所有的都跑完了
        countDownLatch.await();
        timingClock.call("远端数据同步完成");
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
     * 这个只适合用来增量更新，当与其他情况出现在一起的时候，可能发生意外
     */
//    @Scheduled(cron = "0 0/5 * * * ?")
    public synchronized void desk2DB() {
        //找到所有未存入数据库的数据
        List<StockDataRecord> records = this.stockDataRecordService.list(Wrappers.<StockDataRecord>query().lambda().eq(StockDataRecord::getInDb, 0))
                .stream()
                .sorted(Comparator.comparing(StockDataRecord::getDate))
                .collect(Collectors.toList());
        if (records.isEmpty()) {
            log.info("不需要将本地磁盘的数据同步到数据库");
            return;
        }
        log.info("准备将本地磁盘的数据同步到数据库");
        Integer minDate = records.get(0).getDate();
        Integer maxDate = records.get(records.size() - 1).getDate();

        List<StockDayData> list = getListInDBAndDeskV2(records);

        //将16分钟的时间降低到2分钟,精彩绝伦
        QuicklyInsertUtilV2.quicklySaveToDatabase(list, 50_0000, dataSourceProperties);

        LambdaUpdateWrapper<StockDataRecord> eq = Wrappers.<StockDataRecord>update()
                .lambda()
                .ge(StockDataRecord::getDate, minDate)
                .le(StockDataRecord::getDate, maxDate)
                .set(StockDataRecord::getInDb, 1);
        this.stockDataRecordService.update(eq);
        log.info("完成同步!!!本地磁盘的数据同步到数据库");

    }

    private List<StockDayData> getListInDBAndDeskV2(List<StockDataRecord> records) {
        Integer firstDate = records.get(0).getDate();
//        long minDate = Math.max(TimeUtil.offsetLeft(firstDate, 300), 20150101);
        long minDate = 20150101;
        long maxDate = TimeUtil.offsetLeft(firstDate, 1);
        TimingClock t = new TimingClock();
        t.call("Starting to get data from db");
        List<StockDayData> oldList = this.stockDayDataService.getListInDesk(minDate, maxDate);
//         this.stockDayDataService.list(Wrappers.<StockDayData>query().lambda().ge(StockDayData::getDate,minDate).le(StockDayData::getDate,maxDate));
        t.call("Getting data from success");
        List<StockDayData> deskList = records.stream().map(StockDataRecord::getDate)
                .map(date -> {
                    String filePath = stockConfig.getDayStockDataTxt().replace("{date}", String.valueOf(date));
                    return StockDayDataReadUtil.readStockDayData(filePath);
                }).reduce(new ArrayList<>(), (l, r) -> {
                    l.addAll(r);
                    return l;
                });
        t.call("Getting DB data over");
        oldList.addAll(deskList);
        Map<String, List<StockDayData>> map = oldList.stream().collect(Collectors.groupingBy(StockDayData::getCode));
        t.call("Becoming map");

        map.forEach((k, v) -> fixedThreadPool.submit(() -> {
            v.sort(Comparator.comparing(StockDayData::getDate));
            CciUtilV2.calculateAllCCI(v);
        }));
        t.call("sorting and calculate cci");
        return deskList;
    }

    /**
     * 一次性使用的
     * 手工控制
     * 磁盘纠正错误机制
     */
    public synchronized void changeError() {
        List<StockDataRecord> errorRecord = stockDataRecordService.findErrorInDB().stream().limit(200).collect(Collectors.toList());
        if (errorRecord.isEmpty()) {
            log.info("一切正常,无需纠错");
            return;
        }
        log.info("纠错正式开始...");
        String fatherDir = stockConfig.getDayStockPath().substring(0, stockConfig.getDayStockPath().lastIndexOf('\\'));
        File fatherFile = new File(fatherDir);
        if (!fatherFile.exists()) {
            fatherFile.mkdirs();
        }
        final CountDownLatch c = new CountDownLatch(errorRecord.size());
        errorRecord.forEach(entity -> {
            final String date = String.valueOf(entity.getDate());
            try {
                R<String> stringR = PythonUtil.callPython(stockConfig.getDayStockScriptPath(), stockConfig.getDayStockExePath(),
                        stockConfig.getDayStockPath().replace("{date}", date), date, date);
                entity.setMsg(stringR.getMsg());
                this.stockDataRecordService.updateById(entity);
            } catch (IOException | InterruptedException e) {
                log.error(e.toString());
                entity.setMsg(e.getClass().toString() + ":" + e.toString());
                this.stockDataRecordService.updateById(entity);
            }
            c.countDown();
        });

        try {
            c.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<StockDataRecord> errorInDB = stockDataRecordService.findErrorInDB();
        //再次查找的时候，如果都是ok的，那么直接清空所有的StockData，直接重新导入
        if (errorInDB.isEmpty()) {
            //清空所有数据,重新导入
            stockDayDataService.clearData();
            stockDataRecordService.update(Wrappers.<StockDataRecord>update().lambda().set(StockDataRecord::getInDb, 0));
        }
        log.info("纠错完成");

    }


    /**
     * 将db中的数据拿来生成中间文件
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public synchronized void dbCalculate() throws InterruptedException {
        int hHmm = Integer.parseInt(new SimpleDateFormat("HHmm").format(new Date()));
        if (hHmm > 8_00 && hHmm <= 18_00) {
            log.info("上班时间,不计算");
            return;
        }
        StockDataRecord haveDataRecord = this.stockDataRecordService.findHaveDataRecord();
        if (haveDataRecord == null) {
            log.error("没有可以来生成中间文件的日期");
            return;
        }
        //找到可以用的一天
        Integer date = haveDataRecord.getDate();
        //获取磁盘上已经计算完成的股票列表
        Set<String> calculatedCodeList = getCalculatedCodeList(date);
        if(calculatedCodeList.size()>2000){
            log.info("计算数已经超过2000不再进行计算了");
            return;
        }
        //从库里找到不在磁盘上的股票列表
        List<StockDayData> byDate = this.stockDayDataService.findByDate(date).stream().filter(s -> !calculatedCodeList.contains(s.getCode()))
                //打乱顺序
                .sorted((stockDayData, t1) -> {
                    Random random = new Random();
                    return Integer.compare(random.nextInt(100), random.nextInt(100));
                })
                //取出前100个
                .limit(300)
                .collect(Collectors.toList());

        CountDownLatch countDownLatch = new CountDownLatch(byDate.size());
        TimingClock t = new TimingClock();
//        List<StockDayData> all = this.stockDayDataService.list();
//        t.call("Getting data from database");
        byDate.forEach(d -> fixedThreadPool.execute(() -> {
            try {
                TimingClock t2 = new TimingClock();
                t2.call(Thread.currentThread() + ":开始生成数据" + d.getCode());
//                this.stockDayDataService.makeDataByCodeCommon(all, d.getCode());
                this.stockDayDataService.makeDataByCode(d.getCode(), d.getDate().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }));
        countDownLatch.await();
        t.call("股票分数计算完成");

    }

    /**
     * 获取已经计算完成的数据的列表
     *
     * @param date date
     * @return set
     */
    private Set<String> getCalculatedCodeList(Integer date) {
        File file = new File(stockConfig.getResultDir().replace("{date}", String.valueOf(date)));
        File[] files = file.listFiles();
        if (files == null) {
            return new HashSet<>();
        }
        return Arrays.stream(files).map(s -> s.getName().substring(0, s.getName().indexOf('.'))).collect(Collectors.toSet());
    }

    public void test() {
        List<StockDayData> sh603601 = this.stockDayDataService.list(Wrappers.<StockDayData>query().lambda().eq(StockDayData::getCode, "sh603601"));
        sh603601.sort(Comparator.comparing(StockDayData::getDate));
        CciUtilV2.calculateAllCCI(sh603601);
        System.out.println(sh603601.size());

    }
}
