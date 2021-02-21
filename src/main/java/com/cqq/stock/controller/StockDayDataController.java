package com.cqq.stock.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.stock.entity.dto.MakeDataDTO;
import com.cqq.stock.entity.dto.StockListCondition;
import com.cqq.stock.entity.vo.StockData;
import com.cqq.stock.service.StockDayDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("stockDayData")
@AllArgsConstructor
public class StockDayDataController {

    private StockDayDataService stockDayDataService;

    @PostMapping("makeDataByCode")
    public void makeDataByCode(@RequestBody MakeDataDTO makeDataDTO) throws Exception {
        stockDayDataService.makeDataByCode(makeDataDTO.getCode(),makeDataDTO.getDate());
    }

    @PostMapping("list")
    public Page<StockData> list(@RequestBody StockListCondition condition) {
        return stockDayDataService.listByCondition(condition);

    }
}
