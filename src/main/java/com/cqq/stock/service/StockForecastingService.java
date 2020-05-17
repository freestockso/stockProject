package com.cqq.stock.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.entity.Stock;
import com.cqq.stock.entity.StockForecasting;
import com.cqq.stock.entity.StockInfo;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.entity.vo.StockVO;
import com.cqq.stock.mapper.StockForecastingMapper;
import com.cqq.stock.mapper.StockTransactionInfoMapper;
import com.cqq.stock.util.MakeDataUtil;
import com.cqq.stock.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StockForecastingService extends ServiceImpl<StockForecastingMapper, StockForecasting> {

    private StockService stockService;
    private PythonService pythonService;

    public void makeDataByCode(String code) throws Exception {
        List<StockTransactionInfo> stockInfoList = stockService.getCciListByCode(code);
        if (stockInfoList.isEmpty()) {
            log.info("data not enough, sorry! ");
            return;
        }
        StockTransactionInfo lastStock = stockInfoList.get(stockInfoList.size() - 1);
        Long date = lastStock.getDate();

        String dirName = date == null ? UUID.randomUUID().toString() : date.toString();
        MakeDataUtil.generateX(stockInfoList, dirName, code);
        MakeDataUtil.generateY(stockInfoList, dirName, code);
        MakeDataUtil.generateTestData(stockInfoList, dirName, code);
        MakeDataUtil.generateOtherDir(dirName);
    }

    private StockTransactionInfoMapper stockTransactionInfoMapper;

    public void calculateOne(String code, String date) {
        long time1 = System.currentTimeMillis();
        List<StockTransactionInfo> list = stockTransactionInfoMapper.selectList(Wrappers.<StockTransactionInfo>query().lambda()
                .eq(StockTransactionInfo::getCode, code)
        ).stream().skip(28).collect(Collectors.toList());
        if (list.size() < 100) {
            log.info("Data is too low");
            return;
        }
        long time2 = System.currentTimeMillis();
        log.info("get data spend time: {}ms. data.size:{}", time2 - time1, list.size());
        try {
            long time3 = System.currentTimeMillis();
            MakeDataUtil.generateX(list, date, code);
            MakeDataUtil.generateY(list, date, code);
            MakeDataUtil.generateTestData(list, date, code);
            MakeDataUtil.generateY(list, date, code);
            long time4 = System.currentTimeMillis();
            log.info("make data:{}ms", time4 - time3);
            pythonService.callOne(code, date);
            long time5 = System.currentTimeMillis();
            log.info("call python make data: {}ms", time5 - time4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<StockVO> getData(long date) {
        File dir = new File("D:\\newstock\\" + date + "\\result\\");
        File[] files = dir.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        List<String> codeList = Arrays.stream(files)
                .map(File::getName)
                .map(s -> s.substring(0, s.indexOf('.')))
                .collect(Collectors.toList());
        List<StockInfo> stockInfoList = stockService
                .list(Wrappers.<StockInfo>query().lambda().in(StockInfo::getCode, codeList));

        Map<String, StockTransactionInfo> stockMap = stockService.selectByCodeListAndDate(codeList, date);

        List<StockVO> list = stockInfoList.stream()
                .map(s -> {
                    StockVO stockVO = new StockVO();
                    stockVO.setCode(s.getCode().substring(2));
                    StockTransactionInfo stockTransactionInfo = stockMap.get(s.getCode());
                    stockVO.setClosePrice(stockTransactionInfo == null ? null : stockTransactionInfo.getClose());
                    stockVO.setName(s.getName());
                    stockVO.setRate(getRateByCodeAndDate(date, s.getCode()));
                    stockVO.setDate(stockTransactionInfo == null ? null : stockTransactionInfo.getDate());
                    stockVO.setCode(s.getCode());
                    return stockVO;
                })
                .sorted((o1, o2) -> o2.getRate().compareTo(o1.getRate()))
                .collect(Collectors.toList());

        return list;
    }

    /**
     * 根据日期与股票代号获取 赚钱的概率
     *
     * @param date 时间 20200107
     * @param code 股票码 sh000001
     * @return 概率 0.01 属于[0,100.0]
     */
    private double getRateByCodeAndDate(long date, String code) {
        String fileName = "D:\\newstock\\{date}\\result\\{code}.txt"
                .replace("{date}", date + "")
                .replace("{code}", code);
        File file = new File(fileName);
        List<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
                int pos = s.lastIndexOf("%");
                StringBuilder result = new StringBuilder();
                for (int z = pos - 1; z >= 0; z--) {
                    char c = s.charAt(z);
                    if (c >= '0' && c <= '9' || c == '.') {
                        result.insert(0, c);
                    } else {
                        break;
                    }

                }
                list.add(result.toString());

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -100;
        }
        if (list.size() != 20) {
            System.out.println(file.getName() + " data error");
            return -100;

        }
        double low = 0;
        double high = 0;
        for (int j = 0; j < 20; j++) {
            String x = list.get(j);
            double xValue = Double.parseDouble(x);
            if (j < 10) {

                low += xValue;
            } else {
                high += xValue;

            }
        }
        return high / (low + high);
    }
}