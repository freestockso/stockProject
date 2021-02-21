package com.cqq.stock.controller;

import com.cqq.stock.entity.dto.*;
import com.cqq.stock.entity.vo.R;
import com.cqq.stock.service.StockForecastingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("stockForecasting")
@CrossOrigin
public class StockForecastingController {

    private StockForecastingService stockForecastingService;

    @PostMapping("makeDataByCode")
    public void makeDataByCode(@RequestBody MakeDataDTO makeDataDTO) throws Exception {
        stockForecastingService.makeDataByCode(makeDataDTO.getCode());
    }


    @PostMapping("calculateOne")
    public void calculateOne(@RequestBody @Valid CodeDTO codeDTO) {
        stockForecastingService.calculateOne(codeDTO.getCode(), codeDTO.getDate());
    }

    @PostMapping("calculateOneSync")
    public void calculateOneSync(@RequestBody @Valid CodeDTO codeDTO) {
        new Thread(() -> {
            stockForecastingService.calculateOne(codeDTO.getCode(), codeDTO.getDate());
        }).start();

    }

    @RequestMapping("list")
    public R list(@RequestBody @Valid DateDTO dateDTO) {
        return R.ok(stockForecastingService.getData(dateDTO.getDate()));
    }


    @PostMapping("getResult")
    public R getResult(@RequestBody @Valid GetResultDTO getResultDTO) {
        return R.ok(stockForecastingService.getResult(getResultDTO));
    }

    @PostMapping("status")
    public R status(@RequestBody @Valid StatusDTO statusDTO) {
        return R.ok(stockForecastingService.status(statusDTO));
    }
}
