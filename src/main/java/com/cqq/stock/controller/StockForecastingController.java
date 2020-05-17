package com.cqq.stock.controller;

import com.cqq.stock.entity.dto.MakeDataDTO;
import com.cqq.stock.service.StockForecastingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("stockForecasting")
public class StockForecastingController {

    private StockForecastingService stockForecastingService;
    @PostMapping("makeDataByCode")
    public void makeDataByCode(@RequestBody MakeDataDTO makeDataDTO) throws Exception {
        stockForecastingService.makeDataByCode(makeDataDTO.getCode());

    }
}
