package com.cqq.stock.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.constants.StockConfig;
import com.cqq.stock.entity.StockInfo;
import com.cqq.stock.entity.dto.StockListCondition;
import com.cqq.stock.entity.po.StockDataRecord;
import com.cqq.stock.entity.po.StockDayData;
import com.cqq.stock.entity.vo.StockData;
import com.cqq.stock.enums.StockStatusEnum;
import com.cqq.stock.mapper.StockDayDataMapper;
import com.cqq.stock.mapper.StockInfoMapper;
import com.cqq.stock.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.cqq.stock.util.StockResultUtil.getValueList;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
@AllArgsConstructor
public class StockDayDataService extends ServiceImpl<StockDayDataMapper, StockDayData> {

    private PythonService pythonService;

    public void makeDataByCodeCommon(List<StockDayData> all, String code,String date) throws Exception {
        List<StockDayData> list = all.stream().filter(s -> s.getCode().equals(code)).collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparing(StockDayData::getDate))
                .skip(CciUtilV2.N * 2)
                .collect(Collectors.toList());
        makeData(code,date, list);

    }

    public void makeDataByCode(String code,String date) throws Exception {
        TimingClock timingClock = new TimingClock();
        timingClock.call("Starting to get data in makeDataByCode");
        List<StockDayData> list = this.list(Wrappers.<StockDayData>query().lambda().eq(StockDayData::getCode, code))
                .stream()
                .sorted(Comparator.comparing(StockDayData::getDate))
                .skip(CciUtilV2.N * 2)
                .collect(Collectors.toList());
        timingClock.call("over to get data in makeDataByCode");

        makeData(code,date, list);
    }


    private void makeData(String code,String date, List<StockDayData> list) throws Exception {

        if (list.isEmpty()) {
            log.info("data not enough, sorry! ");
            MakeDataUtil.generateNothingResult(stockConfig.getResultTxt(),date,code);
            return;
        }

        String dirName = date == null ? UUID.randomUUID().toString() : date;
        MakeDataUtil.generateX(list, dirName, code);
        MakeDataUtil.generateY(list, dirName, code);
        MakeDataUtil.generateTestData(list, dirName, code);
        MakeDataUtil.generateOtherDir(dirName, "D:\\newstock\\{date}\\result\\", "D:\\newstock\\{date}\\param\\");
        log.info(Thread.currentThread() + " make data success");
        pythonService.callOne(code, String.valueOf(date));
        log.info(Thread.currentThread() + " call success");
    }

    public List<StockDayData> findByDate(Integer date) {
        return this.list(Wrappers.<StockDayData>query().lambda().eq(StockDayData::getDate, date));

    }

    public void clearData() {
        this.getBaseMapper().clearAllData();

    }

    private StockDataRecordService stockDataRecordService;

    public Page<StockData> listByCondition(StockListCondition condition) {
        if (condition.getSortByScore() == null || !condition.getSortByScore()) {
            return getStockDataPageInDB(condition);
        } else {
            StockDataRecord haveDataRecord = this.stockDataRecordService.findHaveDataRecord();
            if (haveDataRecord == null) {
                log.error("无可用数据");
                return new Page<>();
            }
            //找到可以用的一天
            Integer newDate = haveDataRecord.getDate();
            File root = new File("D:\\newstock\\{date}\\result".replace("{date}", String.valueOf(newDate)));
            if (!root.exists()) {
                return new Page<>();
            }
            File[] files = root.listFiles();
            if (files == null) {
                return new Page<>();
            }
            Map<String, Double> code2Score = new HashMap<>();
            for (File file : files) {

                List<Double> valueList = getValueList(file);
                double sum = 0;
                for (int i = 0; i < 20; i++) {
                    sum += valueList.get(i) * (i - 9.5);
                }
                String name = file.getName();
                name = name.substring(0, name.indexOf('.'));
                code2Score.put(name, sum);
            }
            List<StockDayData> stockTransactionInfoList = this.getBaseMapper()
                    .selectList(Wrappers.<StockDayData>query().lambda().eq(StockDayData::getDate, newDate))
                    .stream()
                    .filter(s -> code2Score.get(s.getCode()) != null)
                    .sorted(Comparator.comparing(s -> code2Score.get(s.getCode())))
                    .collect(Collectors.toList());
            List<StockData> res = stockTransactionInfoList.stream().map(s -> {
                StockData stockData = new StockData(s);
                stockData.setStatus(StockStatusEnum.DONE.getValue());
                return stockData;
            }).collect(Collectors.toList());
            return PageUtil.page(res, condition.getCurrent(), condition.getLimit());
        }
    }

    private StockInfoMapper stockInfoMapper;

    private Page<StockData> getStockDataPageInDB(StockListCondition condition) {
        StockDataRecord haveDataRecord = this.stockDataRecordService.findHaveDataRecord();
        if (haveDataRecord == null) {
            log.error("无可用数据");
            return new Page<>();
        }
        //找到可以用的一天
        Integer newDate = haveDataRecord.getDate();
        List<StockInfo> stockInfos = this.stockInfoMapper.selectList(null);
        Map<String, String> codeToName = stockInfos.stream().distinct().collect(toMap(StockInfo::getCode, StockInfo::getName));
        IPage<StockDayData> page = this.getBaseMapper().selectPage(PageUtil.toPage(condition),
                Wrappers.<StockDayData>query().lambda()
                        .eq(StockDayData::getDate, newDate)
                        .le(Objects.nonNull(condition.getMaxPrice()), StockDayData::getClose, condition.getMaxPrice())
                        .ge(Objects.nonNull(condition.getMinPrice()), StockDayData::getClose, condition.getMinPrice())
                        .le(Objects.nonNull(condition.getMaxCCI()), StockDayData::getCci, condition.getMaxCCI())
                        .ge(Objects.nonNull(condition.getMinCCI()), StockDayData::getCci, condition.getMinCCI())
        );
        Set<String> prepareSet = getSet(newDate, "D:\\newstock\\{date}\\logicX");
        Set<String> okSet = getSet(newDate, "D:\\newstock\\{date}\\result");
        List<StockData> result = page.getRecords().stream()
                .map(StockData::new)
                .peek(s -> {
                    String name = codeToName.get(s.getCode());
                    s.setName(name == null ? "unknown" : name);
                }).peek(s -> {
                    if (prepareSet.contains(s.getCode())) {
                        s.setStatus(StockStatusEnum.DOING.getValue());
                    }
                }).peek(s -> {
                    if (okSet.contains(s.getCode())) {
                        s.setStatus(StockStatusEnum.DONE.getValue());
                    }
                })
                .collect(Collectors.toList());

        Page<StockData> stockDataPage = new Page<>();
        stockDataPage.setRecords(result);
        stockDataPage.setCurrent(page.getCurrent());
        stockDataPage.setTotal(page.getTotal());
        stockDataPage.setSize(page.getSize());
        return stockDataPage;
    }

    private Set<String> getSet(Integer newDate, String s2) {
        File file = new File(s2.replace("{date}", newDate.toString()));
        if (file.listFiles() == null) {
            return new HashSet<>();
        }
        return Arrays.stream(((Objects.requireNonNull(file.listFiles()))))
                .map(File::getName)
                .map(s -> s.substring(0, s.indexOf('.')))
                .collect(Collectors.toSet());
    }

    private StockConfig stockConfig;

    private ExecutorService fixedThreadPool;

    /**
     * 原本是从数据库中获取原来的数据，现在改了，直接从磁盘中拿，速度飞快!!!
     *
     * @param minDate
     * @param maxDate
     * @return
     */
    public List<StockDayData> getListInDesk(long minDate, long maxDate) {
        TimingClock t2 = new TimingClock();
        File root = new File(stockConfig.getDayStockDir());
        File[] files = root.listFiles();
        Vector<StockDayData> list = new Vector<>();
        if (files == null) {
            return new ArrayList<>();
        }
        List<File> fileList = Arrays.stream(files).filter(f -> {

            int day = Integer.parseInt(f.getName().substring(0, f.getName().indexOf('.')));
            return day >= minDate && day <= maxDate;
        }).collect(Collectors.toList());
        CountDownLatch countDownLatch = new CountDownLatch(fileList.size());

        fileList.forEach(f -> fixedThreadPool.submit(() -> {
            List<StockDayData> stockDayData = StockDayDataReadUtil.readStockDayData(f.getAbsolutePath());
            list.addAll(stockDayData);
            countDownLatch.countDown();
        }));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.call("read from desk ok");
        return list;
    }
}
