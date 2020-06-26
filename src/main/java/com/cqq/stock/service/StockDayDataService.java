package com.cqq.stock.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.entity.po.StockDayData;
import com.cqq.stock.mapper.StockDayDataMapper;
import com.cqq.stock.util.CciUtilV2;
import com.cqq.stock.util.MakeDataUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class StockDayDataService extends ServiceImpl<StockDayDataMapper, StockDayData> {

    private PythonService pythonService;

    public void makeDataByCode(String code) throws Exception {
        List<StockDayData> list = this.list(Wrappers.<StockDayData>query().lambda().eq(StockDayData::getCode, code))
                .stream()
                .sorted(Comparator.comparing(StockDayData::getDate))
                .skip(CciUtilV2.N * 2)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            log.info("data not enough, sorry! ");
            return;
        }
        StockDayData lastStock = list.get(list.size() - 1);
        Long date = lastStock.getDate();

        String dirName = date == null ? UUID.randomUUID().toString() : date.toString();
        MakeDataUtil.generateX(list, dirName, code);
        MakeDataUtil.generateY(list, dirName, code);
        MakeDataUtil.generateTestData(list, dirName, code);
        MakeDataUtil.generateOtherDir(dirName, "D:\\newstock\\{date}\\result\\", "D:\\newstock\\{date}\\param\\");
        log.info("make data success");
        pythonService.callOne(code, String.valueOf(date));
        log.info("call success");
    }
}
