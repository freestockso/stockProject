package com.cqq.stock.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.entity.StockForecasting;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.mapper.StockForecastingMapper;
import com.cqq.stock.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class StockForecastingService extends ServiceImpl<StockForecastingMapper, StockForecasting> {

    private StockService stockService;

    public void makeDataByCode(String code) throws Exception {
        List<StockTransactionInfo> stockInfoList = stockService.getCciListByCode(code);
        if (stockInfoList.isEmpty()) {
            log.info("data not enough, sorry! ");
            return;
        }
        StockTransactionInfo lastStock = stockInfoList.get(stockInfoList.size() - 1);
        Long date = lastStock.getDate();

        String dirName = date == null ? UUID.randomUUID().toString() : date.toString();
        generateX(stockInfoList, dirName, code);
        generateY(stockInfoList, dirName, code);
        generateTestData(stockInfoList, dirName, code);
        generateOtherDir(dirName);
//        generateParamDir(dirName);
    }

    private void generateOtherDir(String dirName) {
        File resultDir = new File("D:\\newstock\\" + dirName + "\\result\\");
        File paramDir = new File("D:\\newstock\\" + dirName + "\\param\\");
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        if (!paramDir.exists()) {
            paramDir.mkdirs();
        }
    }

    private static void generateX(List<StockTransactionInfo> collect, String dirName, String code) throws IOException {
        BufferedWriter bx = FileUtil.getBufferWriter("D:\\newstock\\" + dirName + "\\logicX\\" + code + ".txt");
        if (bx == null) return;
        for (int i = 14; i < collect.size(); i++) {
            for (int j = i - 14; j < i; j++) {
                if (j != i - 14) {
                    bx.write(" ");
                }
                bx.write(collect.get(j).getCci() / 100 + "");
            }
            bx.write("\r\n");
        }
        bx.close();
    }

    private static void generateY(List<StockTransactionInfo> collect, String dirName, String code) throws IOException {
        BufferedWriter by = FileUtil.getBufferWriter("D:\\newstock\\" + dirName + "\\logicY\\" + code + ".txt");
        if (by == null) return;
        for (int i = 14; i < collect.size(); i++) {

            double todayValue = collect.get(i).getClose().doubleValue();
            double yesterDayValue = collect.get(i - 1).getClose().doubleValue();
            double rate = (todayValue - yesterDayValue) / yesterDayValue * 100;
            for (int j = -10; j <= 9; j++) {
                if (j <= rate && rate <= j + 1) {
                    by.write("1 ");
                } else {
                    by.write("0 ");
                }
            }
            by.write("\r\n");
        }
        by.close();
    }

    private static void generateTestData(List<StockTransactionInfo> stockCalculates, String dirName, String code) throws Exception {
        BufferedWriter bx = FileUtil.getBufferWriter("D:\\newstock\\" + dirName + "\\logicZ\\" + code + ".txt");
        if (bx == null) return;
        int size = stockCalculates.size();
        stockCalculates.subList(size - 14, size).forEach(s -> {
            try {
                bx.write(s.getCci() / 100 + " ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bx.close();
    }
}
