package com.cqq.stock.controller;

import com.cqq.stock.entity.dto.CodeDTO;
import com.cqq.stock.entity.dto.DateDTO;
import com.cqq.stock.entity.dto.MakeDataDTO;
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

    @RequestMapping("list")
    public R list(@RequestBody @Valid DateDTO dateDTO) {
        return R.successData(stockForecastingService.getData(dateDTO.getDate()));
    }

}
